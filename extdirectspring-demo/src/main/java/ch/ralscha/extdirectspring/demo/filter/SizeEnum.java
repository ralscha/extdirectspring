package ch.ralscha.extdirectspring.demo.filter;

public enum SizeEnum {
  SMALL("small"), MEDIUM("medium"), LARGE("large"), EXTRA_LARGE("extra large");

  private String label;

  private SizeEnum(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static SizeEnum find(String label) {
    for (SizeEnum sizeEnum : SizeEnum.values()) {
      if (sizeEnum.getLabel().equals(label)) {
        return sizeEnum;
      }
    }
    return null;
  }
}
