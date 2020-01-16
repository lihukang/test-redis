package com.nhncorp.ooms.redis.model;

import java.io.Serializable;

/**
 * 
 * @ClassName   : UserInfoModel.java
 * @Description : emp模型
 * @author Yin Xueyuan
 * @since 2017/4/2
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2017/4/2       Yin Xueyuan           first create
 * </pre>
 */
public class UserInfoModel implements Serializable,Comparable<UserInfoModel>
 {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5477760898944205168L;
	/**
	 * ID
	 */
	private int id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 年龄
	 */
	private String time;
	/**
	 * 备注
	 */
	private String note;
	/**
	 * 职员类型
	 */
	private String empTp;
	/**
	 * 生日
	 */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getEmpTp() {
		return empTp;
	}

	public void setEmpTp(String empTp) {
		this.empTp = empTp;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((empTp == null) ? 0 : empTp.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((note == null) ? 0 : note.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserInfoModel other = (UserInfoModel) obj;
		if (empTp == null) {
			if (other.empTp != null)
				return false;
		} else if (!empTp.equals(other.empTp))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (note == null) {
			if (other.note != null)
				return false;
		} else if (!note.equals(other.note))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserInfoModel [id=" + id + ", name=" + name + ", time=" + time
				+ ", note=" + note + ", empTp=" + empTp + "]";
	}


	@Override
	public int compareTo(UserInfoModel obj) {
		if(obj instanceof UserInfoModel) {
			UserInfoModel foo = (UserInfoModel) obj;
		    if (this.id > foo.getId()) {
		     return 1;
		    }
		    else if (this.id == foo.getId()) {
		     return 0;
		    }
		    else {
		     return -1;
		    }

		   }
		   return 0;
		  }

	}

	
