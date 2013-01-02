/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ralscha.extdirectspring.generator.bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import ch.ralscha.extdirectspring.generator.ModelFieldBean;
import ch.ralscha.extdirectspring.generator.ModelType;

public class BeanWithoutAnnotations {

	private byte aByte;

	private short aShort;

	private int aInt;

	private long aLong;

	private Byte aByteObject;

	private Short aShortObject;

	private Integer aIntObject;

	private Long aLongObject;

	private BigDecimal aBigDecimal;

	private BigInteger aBigInteger;

	private Calendar aCalendar;

	private GregorianCalendar aSecondCalendar;

	private float aFloat;

	private double aDouble;

	private Float aFloatObject;

	private Double aDoubleObject;

	private String aString;

	private boolean aBoolean;

	private Boolean aBooleanObject;

	private Date aDate;

	private java.sql.Date aSqlDate;

	private Timestamp aTimestamp;

	private DateTime aDateTime;

	private LocalDate aLocalDate;

	public byte getaByte() {
		return aByte;
	}

	public void setaByte(byte aByte) {
		this.aByte = aByte;
	}

	public short getaShort() {
		return aShort;
	}

	public void setaShort(short aShort) {
		this.aShort = aShort;
	}

	public int getaInt() {
		return aInt;
	}

	public void setaInt(int aInt) {
		this.aInt = aInt;
	}

	public long getaLong() {
		return aLong;
	}

	public void setaLong(long aLong) {
		this.aLong = aLong;
	}

	public Byte getaByteObject() {
		return aByteObject;
	}

	public void setaByteObject(Byte aByteObject) {
		this.aByteObject = aByteObject;
	}

	public Short getaShortObject() {
		return aShortObject;
	}

	public void setaShortObject(Short aShortObject) {
		this.aShortObject = aShortObject;
	}

	public Integer getaIntObject() {
		return aIntObject;
	}

	public void setaIntObject(Integer aIntObject) {
		this.aIntObject = aIntObject;
	}

	public Long getaLongObject() {
		return aLongObject;
	}

	public void setaLongObject(Long aLongObject) {
		this.aLongObject = aLongObject;
	}

	public BigDecimal getaBigDecimal() {
		return aBigDecimal;
	}

	public void setaBigDecimal(BigDecimal aBigDecimal) {
		this.aBigDecimal = aBigDecimal;
	}

	public BigInteger getaBigInteger() {
		return aBigInteger;
	}

	public void setaBigInteger(BigInteger aBigInteger) {
		this.aBigInteger = aBigInteger;
	}

	public float getaFloat() {
		return aFloat;
	}

	public void setaFloat(float aFloat) {
		this.aFloat = aFloat;
	}

	public double getaDouble() {
		return aDouble;
	}

	public void setaDouble(double aDouble) {
		this.aDouble = aDouble;
	}

	public Float getaFloatObject() {
		return aFloatObject;
	}

	public void setaFloatObject(Float aFloatObject) {
		this.aFloatObject = aFloatObject;
	}

	public Double getaDoubleObject() {
		return aDoubleObject;
	}

	public void setaDoubleObject(Double aDoubleObject) {
		this.aDoubleObject = aDoubleObject;
	}

	public String getaString() {
		return aString;
	}

	public void setaString(String aString) {
		this.aString = aString;
	}

	public boolean isaBoolean() {
		return aBoolean;
	}

	public void setaBoolean(boolean aBoolean) {
		this.aBoolean = aBoolean;
	}

	public Boolean getaBooleanObject() {
		return aBooleanObject;
	}

	public void setaBooleanObject(Boolean aBooleanObject) {
		this.aBooleanObject = aBooleanObject;
	}

	public Date getaDate() {
		return aDate;
	}

	public void setaDate(Date aDate) {
		this.aDate = aDate;
	}

	public java.sql.Date getaSqlDate() {
		return aSqlDate;
	}

	public void setaSqlDate(java.sql.Date aSqlDate) {
		this.aSqlDate = aSqlDate;
	}

	public Timestamp getaTimestamp() {
		return aTimestamp;
	}

	public void setaTimestamp(Timestamp aTimestamp) {
		this.aTimestamp = aTimestamp;
	}

	public DateTime getaDateTime() {
		return aDateTime;
	}

	public void setaDateTime(DateTime aDateTime) {
		this.aDateTime = aDateTime;
	}

	public LocalDate getaLocalDate() {
		return aLocalDate;
	}

	public void setaLocalDate(LocalDate aLocalDate) {
		this.aLocalDate = aLocalDate;
	}

	public Calendar getaCalendar() {
		return aCalendar;
	}

	public void setaCalendar(Calendar aCalendar) {
		this.aCalendar = aCalendar;
	}

	public GregorianCalendar getaSecondCalendar() {
		return aSecondCalendar;
	}

	public void setaSecondCalendar(GregorianCalendar aSecondCalendar) {
		this.aSecondCalendar = aSecondCalendar;
	}

	public static List<ModelFieldBean> expectedFields = new ArrayList<ModelFieldBean>();
	static {
		expectedFields.add(new ModelFieldBean("aByte", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aShort", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aInt", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aLong", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aByteObject", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aShortObject", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aIntObject", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aLongObject", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aBigDecimal", ModelType.FLOAT));
		expectedFields.add(new ModelFieldBean("aBigInteger", ModelType.INTEGER));
		expectedFields.add(new ModelFieldBean("aCalendar", ModelType.DATE));
		expectedFields.add(new ModelFieldBean("aSecondCalendar", ModelType.DATE));
		expectedFields.add(new ModelFieldBean("aFloat", ModelType.FLOAT));
		expectedFields.add(new ModelFieldBean("aDouble", ModelType.FLOAT));
		expectedFields.add(new ModelFieldBean("aFloatObject", ModelType.FLOAT));
		expectedFields.add(new ModelFieldBean("aDoubleObject", ModelType.FLOAT));
		expectedFields.add(new ModelFieldBean("aString", ModelType.STRING));
		expectedFields.add(new ModelFieldBean("aBoolean", ModelType.BOOLEAN));
		expectedFields.add(new ModelFieldBean("aBooleanObject", ModelType.BOOLEAN));
		expectedFields.add(new ModelFieldBean("aDate", ModelType.DATE));
		expectedFields.add(new ModelFieldBean("aSqlDate", ModelType.DATE));
		expectedFields.add(new ModelFieldBean("aTimestamp", ModelType.DATE));
		expectedFields.add(new ModelFieldBean("aDateTime", ModelType.DATE));
		expectedFields.add(new ModelFieldBean("aLocalDate", ModelType.DATE));
	}

}
