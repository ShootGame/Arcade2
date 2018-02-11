package pl.themolka.arcade.permission;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.arcade.xml.XMLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XMLPermissions {
    private final Document document;

    public XMLPermissions(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return this.document;
    }

    //
    // Groups
    //

    public List<Group> readGroups() throws JDOMException {
        List<Group> results = new ArrayList<>();

        Element groupsElement = this.getDocument().getRootElement().getChild("groups");
        if (groupsElement == null) {
            return results;
        }

        // read groups
        for (Element groupElement : groupsElement.getChildren("group")) {
            // id
            String id = groupElement.getAttributeValue("id");
            if (id == null) {
                continue;
            }

            String family = groupElement.getAttributeValue("family");
            if (family == null) {
                family = Group.DEFAULT_FAMILY;
            }

            // name
            String name = groupElement.getAttributeValue("name");
            if (name == null) {
                name = id.toUpperCase();
            }

            // prefix
            String prefix = groupElement.getAttributeValue("prefix");
            if (prefix != null) {
                prefix = XMLParser.parseMessage(prefix);
            }

            // default
            boolean def = XMLParser.parseBoolean(groupElement.getAttributeValue("default"), false);

            // operator
            boolean operator = false;
            if (groupElement.getChild("operator") != null) {
                operator = true;
            }

            Group group = new Group(id, groupElement);
            group.setDefault(def);
            group.setFamily(family);
            group.setName(name);
            group.setOperator(operator);
            group.setPrefix(prefix);

            // register
            results.add(group);
        }

        // load groups
        for (Group group : results) {
            for (Permission permission : this.getPermissions(group.getElement().getChildren())) {
                group.addPermission(permission);
            }
        }

        // load group parents
        for (Group group : results) {
            for (Group parent : this.getInherit(results, group.getElement().getChildren("inherit"))) {
                if (parent.isOperator() && !group.isOperator()) {
                    group.setOperator(true); // inherit operators
                }

                for (Permission permission : parent.getPermissions()) {
                    if (!group.contains(permission)) {
                        group.addPermission(permission);
                    }
                }
            }
        }

        return results;
    }

    private Group getGroup(List<Group> groups, String id) {
        for (Group group : groups) {
            if (group.getId().equals(id)) {
                return group;
            }
        }

        return null;
    }

    private List<Group> getInherit(List<Group> source, List<Element> inherit) {
        List<Group> results = new ArrayList<>();
        for (Element element : inherit) {
            String id = element.getValue();
            Group group = this.getGroup(source, id);

            if (group != null) {
                results.add(group);
            }
        }

        return results;
    }

    private List<Permission> getPermissions(List<Element> source) {
        List<Permission> results = new ArrayList<>();
        for (Element permissionElement : source) {
            String elementName = permissionElement.getName();

            Permission permission = null;
            if (elementName.equalsIgnoreCase("grant")) {
                permission = new Permission(permissionElement.getValue(), true);
            } else if (elementName.equalsIgnoreCase("revoke")) {
                permission = new Permission(permissionElement.getValue(), false);
            }

            if (permission != null) {
                results.add(permission);
            }
        }

        return results;
    }

    //
    // Players
    //

    public ClientPermissionStorage readPlayers(List<Group> groupList, Group defaultGroup) throws JDOMException {
        ClientPermissionStorage result = new ClientPermissionStorage(defaultGroup);

        Element playersElement = this.getDocument().getRootElement().getChild("players");
        if (playersElement == null) {
            return result;
        }

        // read players
        for (Element playerElement : playersElement.getChildren("player")) {
            // uuid
            String uuidString = playerElement.getAttributeValue("uuid");
            if (uuidString == null) {
                continue;
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException ex) {
                continue;
            }

            // groups
            List<Group> groups = new ArrayList<>();
            for (Element groupElement : playerElement.getChildren("group")) {
                Group group = this.getGroup(groupList, groupElement.getValue());
                if (group != null) {
                    groups.add(group);
                }
            }

            // register
            if (!groups.isEmpty()) {
                result.put(uuid, groups.toArray(new Group[groups.size()]));
            }
        }

        return result;
    }
}
