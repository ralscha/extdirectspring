package ch.ralscha.starter.entity;


import java.io.Serializable;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QAbstractPersistable is a Querydsl query type for AbstractPersistable
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractPersistable extends EntityPathBase<AbstractPersistable<? extends java.io.Serializable>> {

    private static final long serialVersionUID = 2121340145;

    public final SimplePath<java.io.Serializable> id = createSimple("id", java.io.Serializable.class);

    public QAbstractPersistable(BeanPath<? extends AbstractPersistable<? extends java.io.Serializable>> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    @SuppressWarnings("unchecked")
    public QAbstractPersistable(PathMetadata<?> metadata) {
        super((Class<? extends AbstractPersistable<? extends Serializable>>)AbstractPersistable.class, metadata);
    }

}

