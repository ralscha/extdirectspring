package ch.ralscha.starter.entity;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = 1856413137;

    public static final QRole role = new QRole("role");

    public final QAbstractPersistable _super = new QAbstractPersistable(this);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QRole(String variable) {
        super(Role.class, forVariable(variable));
    }

    public QRole(BeanPath<? extends Role> entity) {
        super(entity.getType(), entity.getMetadata());
    }

    public QRole(PathMetadata<?> metadata) {
        super(Role.class, metadata);
    }

}

