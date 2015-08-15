package ch.ralscha.extdirectspring.bean;

public class BeanMethod {
	private final String bean;
	private final String method;
	private final Object data;

	private int tid;

	public BeanMethod(String bean, String method) {
		this(bean, method, null);
	}

	public BeanMethod(String bean, String method, Object data) {
		this.bean = bean;
		this.method = method;
		this.data = data;
	}

	public String getBean() {
		return bean;
	}

	public String getMethod() {
		return method;
	}

	public Object getData() {
		return data;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

}
