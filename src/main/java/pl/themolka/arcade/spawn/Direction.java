package pl.themolka.arcade.spawn;

public interface Direction {
    Direction CONSTANT = new ConstantDirection();
    Direction ENTITY = new EntityDirection();
    Direction RELATIVE = new RelativeDirection();
    Direction TRANSLATE = new TranslateDirection();

    default float getYaw(float constant, float entity) {
        return this.getValue(constant, entity);
    }

    default float getPitch(float constant, float entity) {
        return this.getValue(constant, entity);
    }

    default float getValue(float constant, float entity) {
        return entity;
    }
}

class ConstantDirection implements Direction {
    @Override
    public float getValue(float constant, float entity) {
        return constant;
    }
}

class EntityDirection implements Direction {
    @Override
    public float getValue(float constant, float entity) {
        return entity;
    }
}

class RelativeDirection implements Direction {
    static final float ZERO = 0F;

    @Override
    public float getValue(float constant, float entity) {
        return entity != ZERO ? -entity : ZERO;
    }
}

class TranslateDirection implements Direction {
    @Override
    public float getValue(float constant, float entity) {
        return entity + constant;
    }
}
