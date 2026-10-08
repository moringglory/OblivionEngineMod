package oblivionengine.content;

import mindustry.content.Weathers;
import mindustry.gen.Groups;
import mindustry.gen.WeatherState;
import mindustry.type.Weather;

public class OEWeather {
    public static boolean isSuspendParticles() {
        for (WeatherState state : Groups.weather) {
            if (state != null && state.weather == Weathers.suspendParticles) {
                return true;
            }
        }
        return false;
    }
    /** 当前是否有天气 */
    public static boolean isClear() {
        for (WeatherState state : Groups.weather) {
            if (state != null && state.weather == null) {
                return false;
            }
        }
        return true;
    }
    /** 当前是否有雨 */
    public static boolean isRaining() {
        return isActive(Weathers.rain);
    }
    /** 当前是否有雪 */
    public static boolean isSnowing() {
        return isActive(Weathers.snow);
    }
    /** 当前是否有沙尘暴 */
    public static boolean isSandstorm() {
        return isActive(Weathers.sandstorm);
    }
    /** 当前是否有孢子雨 */
    public static boolean isSporestorm() {
        return isActive(Weathers.sporestorm);
    }
    /** 当前是否有雾 */
    public static boolean isFog() {
        return isActive(Weathers.fog);
    }
    /** 判断当前天气 */
    public static boolean isActive(Weather weather) {
        if (weather == null) return false;
        for (WeatherState state : Groups.weather) {
            if (state != null && state.weather == weather) {
                return true;
            }
        }
        return false;
    }
}