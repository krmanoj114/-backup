package com.tpex.invoice.dto;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SsLineGroupBoxDTO {
	
	private String groupCode;

	@Override
	public int hashCode() {
		return Objects.hash(groupCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SsLineGroupBoxDTO other = (SsLineGroupBoxDTO) obj;
		return Objects.equals(groupCode, other.groupCode);
	}
	
}
