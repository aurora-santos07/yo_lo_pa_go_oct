package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Tax {
	@PrimaryKey
	private Long _id;
	private String name;

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
