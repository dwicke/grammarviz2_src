package edu.gmu.connectGI;

public enum GrammarIndcutionMethod {

	SEQUITUR(0), REPAIR(1);

	private final int index;

	GrammarIndcutionMethod(int index) {
		this.index = index;
	}

	/**
	 * Gets the integer index of the instance.
	 * 
	 * @return integer key of the instance.
	 */
	public int index() {
		return index;
	}

	public static GrammarIndcutionMethod forValue(int value) {
		switch (value) {
		case 0:
			return GrammarIndcutionMethod.SEQUITUR;
		case 1:
			return GrammarIndcutionMethod.REPAIR;
		default:
			throw new RuntimeException("Unknown index:" + value);
		}
	}

}
