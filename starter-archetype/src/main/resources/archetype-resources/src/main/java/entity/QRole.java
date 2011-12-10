#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.entity;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRole extends EntityPathBase<Role> {

	private static final long serialVersionUID = 1856413137;

	public static final QRole role = new QRole("role");

	public final org.springframework.data.jpa.domain.QAbstractPersistable _super = new org.springframework.data.jpa.domain.QAbstractPersistable(
			this);

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public final StringPath name = createString("name");

	public QRole(String variable) {
		super(Role.class, forVariable(variable));
	}

	public QRole(Path<? extends Role> entity) {
		super(entity.getType(), entity.getMetadata());
	}

	public QRole(PathMetadata<?> metadata) {
		super(Role.class, metadata);
	}

}
