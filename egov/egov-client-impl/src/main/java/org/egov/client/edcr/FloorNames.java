package org.egov.client.edcr;

public class FloorNames {
	private static final String BASEMENT_LEVEL_ONE = "Basement level 1";
	private static final String BASEMENT_LEVEL_TWO = "Basement level 2";
	private static final String BASEMENT_LEVEL_THREE = "Basement level 3";
	private static final String BASEMENT_LEVEL_FOUR = "Basement level 4";
	private static final String GROUND_FLOOR = "Ground Floor";
	private static final String FIRST_FLOOR = "First Floor";
	private static final String SECOND_FLOOR = "Second Floor";
	private static final String THIRD_FLOOR = "Third Floor";
	private static final String FOURTH_FLOOR = "Fourth Floor";
	private static final String FIFTH_FLOOR = "Fith Floor";
	private static final String SIXTH_FLOOR = "Sixth Floor";
	private static final String SEVENTH_FLOOR = "Seventh Floor";
	private static final String EIGHTH_FLOOR = "Eighth Floor";
	private static final String NINETH_FLOOR = "Nineth Floor";
	private static final String TENTH_FLOOR = "Tenth Floor";
	private static final String STILT_FLOOR = "Stilt Floor";

	static String getFloorNames(Integer number) {
		switch (number) {
		case -4:
			return BASEMENT_LEVEL_FOUR;
		case -3:
			return BASEMENT_LEVEL_THREE;
		case -2:
			return BASEMENT_LEVEL_TWO;
		case -1:
			return BASEMENT_LEVEL_ONE;
		case 0:
			return GROUND_FLOOR;
		case 1:
			return FIRST_FLOOR;
		case 2:
			return SECOND_FLOOR;
		case 3:
			return THIRD_FLOOR;
		case 4:
			return FOURTH_FLOOR;
		case 5:
			return FIFTH_FLOOR;
		case 6:
			return SIXTH_FLOOR;
		case 7:
			return SEVENTH_FLOOR;
		case 8:
			return EIGHTH_FLOOR;
		case 9:
			return NINETH_FLOOR;
		case 10:
			return TENTH_FLOOR;
		default:
			return STILT_FLOOR;
		}
	}
}
