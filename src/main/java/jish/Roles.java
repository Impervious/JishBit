package jish;

public enum Roles {
    ZAC("73416411443113984", "109113911038464000", "193972344312692736", "Zac"),
    TROY("109109946565537792", "109114673709760512", "193972344312692736", "Troy"),
    JEREMY("109110807308029952", "109114986596454400", "193972344312692736", "Jeremy"),
    JOSH("73463573900173312", "109114531648647168", "193972344312692736", "Josh"),
    GIBS("109109787517571072", "109115360581521408", "193972344312692736", "Gibs"),
    NAYR("109109783952371712", "109112910340435968", "193972344312692736", "Ryan"),
    JOSEPH("110508617974697984", "184730166474440705", "193972344312692736", "Tates"),
    CAMERON("270992426590666773", "270994427898494977", "193972344312692736", "Cameron"),
    TEST("194959408243933186", "109113911038464000", "193972344312692736", "Test");

    public String user;
    public String role;
    public String human;
    public String nick;

    Roles(String user, String role, String human, String nick) {
        this.user = user;
        this.role = role;
        this.human = human;
        this.nick = nick;
    }

    public static Roles getUserRole(String user) {
        for (Roles role : values()) {
            if (role.user.equalsIgnoreCase(user)) {
                return role;
            }
        }
        return null;
    }
}
