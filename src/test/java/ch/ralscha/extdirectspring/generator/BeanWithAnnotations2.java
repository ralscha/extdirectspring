package ch.ralscha.extdirectspring.generator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Model(value = "Sch.Bean2", idProperty = "id", paging = false, readMethod = "read")
public class BeanWithAnnotations2 extends Base {

	private String name;

	@ModelField(dateFormat = "c")
	private Date dob;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public static List<ModelFieldBean> expectedFields = new ArrayList<ModelFieldBean>();
	static {

		ModelFieldBean field = new ModelFieldBean("id", ModelType.INTEGER);
		expectedFields.add(field);

		field = new ModelFieldBean("name", ModelType.STRING);
		expectedFields.add(field);

		field = new ModelFieldBean("dob", ModelType.DATE);
		field.setDateFormat("c");
		expectedFields.add(field);

	}

}
