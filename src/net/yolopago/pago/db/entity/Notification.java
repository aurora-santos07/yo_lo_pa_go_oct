package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notification {
	@PrimaryKey
	private Long _id;
	private String content;

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
