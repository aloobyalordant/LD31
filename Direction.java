public enum Direction {
	UP,
	DOWN,
	LEFT,
	RIGHT;

	static Direction fromString(String directionString) {
		if (directionString == null || "NULL".equals(directionString)) {
			return null;
		}

		return Direction.valueOf(Direction.class, directionString);
	}
};
