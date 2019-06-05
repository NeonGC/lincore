package ru.lodes.lincore.utils;

public class TimeUtil {

    public static String formatSecondsShort(int seconds) {
        if (seconds <= 59) { //59 секунд
            return plurals(seconds, "секунда", "секунды", "секунд");
        }
        if (seconds <= 3540) { //59 минут
            return plurals((int) Math.ceil(seconds / 60.0F), "минута", "минуты", "минут");
        }
        if (seconds <= 82800) { //23 часа
            return plurals((int) Math.ceil(seconds / 3600.0F), "час", "часа", "часов");
        }
        if (seconds <= 2505600) { //29 дней
            return plurals((int) Math.ceil(seconds / 86400.0F), "день", "дня", "дней");
        }
        if (seconds <= 28512000) { //11 месяцев
            return plurals((int) Math.ceil(seconds / 2592000.0F), "месяц", "месяца", "месяцев");
        }
        return plurals((int) Math.ceil(seconds / 3.1104E7F), "год", "года", "лет");
    }

    public static String plurals(int n, String form1, String form2, String form3) {
        int orig = n;
        if (n == 0) {
            return orig + " " + form3;
        }
        n = Math.abs(n) % 100;
        if ((n > 10) && (n < 20)) {
            return orig + " " + form3;
        }
        n %= 10;
        if ((n > 1) && (n < 5)) {
            return orig + " " + form2;
        }
        if (n == 1) {
            return orig + " " + form1;
        }
        return orig + " " + form3;
    }
}
