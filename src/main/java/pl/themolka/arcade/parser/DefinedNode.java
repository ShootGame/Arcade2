package pl.themolka.arcade.parser;

import pl.themolka.arcade.dom.Node;

import java.util.List;

public interface DefinedNode {
    List<Node> define(Node source);
}
