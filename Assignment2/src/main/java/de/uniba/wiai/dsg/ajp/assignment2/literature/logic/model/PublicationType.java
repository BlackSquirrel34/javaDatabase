package de.uniba.wiai.dsg.ajp.assignment2.literature.logic.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import static de.uniba.wiai.dsg.ajp.assignment2.literature.Main.globalDBscope;

	@XmlEnum(String.class)
	public enum PublicationType {
		@XmlEnumValue("article") ARTICLE,
		@XmlEnumValue("techreport") TECHREPORT,
		@XmlEnumValue("book") BOOK,
		@XmlEnumValue("masterthesis") MASTERSTHESIS,
		@XmlEnumValue("phdthesis") PHDTHESIS,
		@XmlEnumValue("inproceedings") INPROCEEDINGS

	}
// compare to solution of others
// problem: how do anythign useful with this enum from other classes?


