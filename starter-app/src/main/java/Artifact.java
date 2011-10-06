public class Artifact {
	private String groupId;
	private String artifact;

	public Artifact(String groupId, String artifact) {
		this.groupId = groupId;
		this.artifact = artifact;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifact() {
		return artifact;
	}

	public boolean is(String group, String arti) {
		return this.groupId.equals(group) && this.artifact.equals(arti);
	}

}
