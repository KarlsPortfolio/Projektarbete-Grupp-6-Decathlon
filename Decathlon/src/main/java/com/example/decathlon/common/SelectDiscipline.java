package com.example.decathlon.common;

public class SelectDiscipline {

	private static final String[] DECATHLON_DISCIPLINES = {
			"100m (s)",
			"Long Jump (cm)",
			"Shot Put (m)",
			"High Jump (cm)",
			"400m (s)",
			"110m Hurdles (s)",
			"Discus (m)",
			"Pole Vault (cm)",
			"Javelin (m)",
			"1500m (s)"
	};

	private static final String[] HEPTATHLON_DISCIPLINES = {
			"100m Hurdles (s)",
			"High Jump (cm)",
			"Shot Put (m)",
			"200m (s)",
			"Long Jump (cm)",
			"Javelin (m)",
			"800m (s)"
	};

	public String select(int choice) {
		return selectDecathlon(choice);
	}

	public String selectDecathlon(int choice) {
		switch (choice) {
			case 1:
				return "100m (s)";
			case 2:
				return "Long Jump (cm)";
			case 3:
				return "Shot Put (m)";
			case 4:
				return "High Jump (cm)";
			case 5:
				return "400m (s)";
			case 6:
				return "110m Hurdles (s)";
			case 7:
				return "Discus (m)";
			case 8:
				return "Pole Vault (cm)";
			case 9:
				return "Javelin (m)";
			case 10:
				return "1500m (s)";
			default:
				return "";
		}
	}

	public String selectHeptathlon(int choice) {
		switch (choice) {
			case 1:
				return "100m Hurdles (s)";
			case 2:
				return "High Jump (cm)";
			case 3:
				return "Shot Put (m)";
			case 4:
				return "200m (s)";
			case 5:
				return "Long Jump (cm)";
			case 6:
				return "Javelin (m)";
			case 7:
				return "800m (s)";
			default:
				return "";
		}
	}

	public String[] getDecathlonDisciplines() {
		return DECATHLON_DISCIPLINES.clone();
	}

	public String[] getHeptathlonDisciplines() {
		return HEPTATHLON_DISCIPLINES.clone();
	}
}