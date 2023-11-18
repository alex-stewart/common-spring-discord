package fun.pancakes.commonspringdiscord.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseColor {

    public static final Color ERROR = new Color(255, 0, 0);
    public static final Color WARNING = new Color(255, 255, 0);
    public static final Color SUCCESS = new Color(0, 255, 0);
    public static final Color DEFAULT = new Color(132, 210, 195);

}
