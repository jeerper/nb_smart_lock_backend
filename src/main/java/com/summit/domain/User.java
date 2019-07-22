package com.summit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName(value = "user")
public class User {
	@TableId(value = "id", type = IdType.ID_WORKER_STR)
	private String id;
	@TableField(value = "name")
	private String name;
	@TableField(value = "createTime")
	private Date createTime;
	@TableField(value = "age")
	private Integer age;
	@TableField(value = "phone")
	private String phone;
	@TableField(value = "sex")

	private String sex;

	@TableField(value = "email")

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", createTime=" + createTime + ", age=" + age + ", phone=" + phone
				+ ", sex=" + sex + "]";
	}

}
