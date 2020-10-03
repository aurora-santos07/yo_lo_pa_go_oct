package net.yolopago.pago.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TicketHeader {
	@PrimaryKey
	private Long _id;
	private Integer lineType;
	private String content;
	private Boolean bold;
	private String font;
	private Integer fontSize;
	private Long idTicketLayout;

	public Long get_id() {
		return _id;
	}

	public void set_id(Long _id) {
		this._id = _id;
	}

	public Integer getLineType() {
		return lineType;
	}

	public void setLineType(Integer lineType) {
		this.lineType = lineType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getBold() {
		return bold;
	}

	public void setBold(Boolean bold) {
		this.bold = bold;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public Long getIdTicketLayout() {
		return idTicketLayout;
	}

	public void setIdTicketLayout(Long idTicketLayout) {
		this.idTicketLayout = idTicketLayout;
	}
}
