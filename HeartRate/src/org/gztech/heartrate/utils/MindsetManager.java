package org.gztech.heartrate.utils;

/**
 * @author 作者:
 * @date 2016-4-26 上午6:29:08
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class MindsetManager {

	/**
	 * 根据心率的起伏变化，将心率分为四种情况：
	 */

	// RATE_ANGRY=1；生气
	public static final int RATE_ANGRY = 1;
	// RATE_CALMNESS=2； 平静
	public static final int RATE_CALMNESS = 2;
	// RATE_HAPPY=3；开心
	public static final int RATE_HAPPY = 3;
	// RATE_EXCITING=4；兴奋
	public static final int RATE_EXCITING = 4;

	// 心率变化的百分比，允许为负数，正数为上升x%，负数为下跌x%
	private static int[] range;

	public MindsetManager() {
		super();

	}

	public int getMindset(int[] range) {

		int upRange = range[4];
		int downRange = range[5];

		if (upRange > downRange) {

			if (upRange - downRange >= 8) {

				return RATE_EXCITING;

			} else if (upRange - downRange > 0 && upRange - downRange < 5) {

				return RATE_HAPPY;
			}
		} else {

			if (downRange - upRange > 0 && downRange - upRange < 5) {

				return RATE_HAPPY;
			}
		}
		return 2;
	}

	public static String getMindsetString(int mindset) {

		switch (mindset) {
		case RATE_ANGRY:

			return "ANGRY";

		case RATE_CALMNESS:

			return "CALMNESS";

		case RATE_HAPPY:

			return "HAPPY";

		case RATE_EXCITING:

			return "EXCITING";
		}
		return null;
	}

}
