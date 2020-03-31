package com.zylitics.btbr.webdriver.constants;

import com.google.common.collect.ImmutableMap;
import com.zylitics.zwl.datatype.StringZwlValue;
import com.zylitics.zwl.datatype.ZwlValue;
import org.openqa.selenium.support.Colors;

import java.util.Map;

public class Colorz {
  
  public static Map<String, ZwlValue> asMap() {
    return ImmutableMap.<String, ZwlValue>builder()
        .put("transparent", color(Colors.TRANSPARENT))
        .put("aliceblue", color(Colors.ALICEBLUE))
        .put("antiquewhite", color(Colors.ANTIQUEWHITE))
        .put("aqua", color(Colors.AQUA))
        .put("aquamarine", color(Colors.AQUAMARINE))
        .put("azure", color(Colors.AZURE))
        .put("beige", color(Colors.BEIGE))
        .put("bisque", color(Colors.BISQUE))
        .put("black", color(Colors.BLACK))
        .put("blanchedalmond", color(Colors.BLANCHEDALMOND))
        .put("blue", color(Colors.BLUE))
        .put("blueviolet", color(Colors.BLUEVIOLET))
        .put("brown", color(Colors.BROWN))
        .put("burlywood", color(Colors.BURLYWOOD))
        .put("cadetblue", color(Colors.CADETBLUE))
        .put("chartreuse", color(Colors.CHARTREUSE))
        .put("chocolate", color(Colors.CHOCOLATE))
        .put("coral", color(Colors.CORAL))
        .put("cornflowerblue", color(Colors.CORNFLOWERBLUE))
        .put("cornsilk", color(Colors.CORNSILK))
        .put("crimson", color(Colors.CRIMSON))
        .put("cyan", color(Colors.CYAN))
        .put("darkblue", color(Colors.DARKBLUE))
        .put("darkcyan", color(Colors.DARKCYAN))
        .put("darkgoldenrod", color(Colors.DARKGOLDENROD))
        .put("darkgray", color(Colors.DARKGRAY))
        .put("darkgreen", color(Colors.DARKGREEN))
        .put("darkgrey", color(Colors.DARKGREY))
        .put("darkkhaki", color(Colors.DARKKHAKI))
        .put("darkmagenta", color(Colors.DARKMAGENTA))
        .put("darkolivegreen", color(Colors.DARKOLIVEGREEN))
        .put("darkorange", color(Colors.DARKORANGE))
        .put("darkorchid", color(Colors.DARKORCHID))
        .put("darkred", color(Colors.DARKRED))
        .put("darksalmon", color(Colors.DARKSALMON))
        .put("darkseagreen", color(Colors.DARKSEAGREEN))
        .put("darkslateblue", color(Colors.DARKSLATEBLUE))
        .put("darkslategray", color(Colors.DARKSLATEGRAY))
        .put("darkslategrey", color(Colors.DARKSLATEGREY))
        .put("darkturquoise", color(Colors.DARKTURQUOISE))
        .put("darkviolet", color(Colors.DARKVIOLET))
        .put("deeppink", color(Colors.DEEPPINK))
        .put("deepskyblue", color(Colors.DEEPSKYBLUE))
        .put("dimgray", color(Colors.DIMGRAY))
        .put("dimgrey", color(Colors.DIMGREY))
        .put("dodgerblue", color(Colors.DODGERBLUE))
        .put("firebrick", color(Colors.FIREBRICK))
        .put("floralwhite", color(Colors.FLORALWHITE))
        .put("forestgreen", color(Colors.FORESTGREEN))
        .put("fuchsia", color(Colors.FUCHSIA))
        .put("gainsboro", color(Colors.GAINSBORO))
        .put("ghostwhite", color(Colors.GHOSTWHITE))
        .put("gold", color(Colors.GOLD))
        .put("goldenrod", color(Colors.GOLDENROD))
        .put("gray", color(Colors.GRAY))
        .put("grey", color(Colors.GREY))
        .put("green", color(Colors.GREEN))
        .put("greenyellow", color(Colors.GREENYELLOW))
        .put("honeydew", color(Colors.HONEYDEW))
        .put("hotpink", color(Colors.HOTPINK))
        .put("indianred", color(Colors.INDIANRED))
        .put("indigo", color(Colors.INDIGO))
        .put("ivory", color(Colors.IVORY))
        .put("khaki", color(Colors.KHAKI))
        .put("lavender", color(Colors.LAVENDER))
        .put("lavenderblush", color(Colors.LAVENDERBLUSH))
        .put("lawngreen", color(Colors.LAWNGREEN))
        .put("lemonchiffon", color(Colors.LEMONCHIFFON))
        .put("lightblue", color(Colors.LIGHTBLUE))
        .put("lightcoral", color(Colors.LIGHTCORAL))
        .put("lightcyan", color(Colors.LIGHTCYAN))
        .put("lightgoldenrodyellow", color(Colors.LIGHTGOLDENRODYELLOW))
        .put("lightgray", color(Colors.LIGHTGRAY))
        .put("lightgreen", color(Colors.LIGHTGREEN))
        .put("lightgrey", color(Colors.LIGHTGREY))
        .put("lightpink", color(Colors.LIGHTPINK))
        .put("lightsalmon", color(Colors.LIGHTSALMON))
        .put("lightseagreen", color(Colors.LIGHTSEAGREEN))
        .put("lightskyblue", color(Colors.LIGHTSKYBLUE))
        .put("lightslategray", color(Colors.LIGHTSLATEGRAY))
        .put("lightslategrey", color(Colors.LIGHTSLATEGREY))
        .put("lightsteelblue", color(Colors.LIGHTSTEELBLUE))
        .put("lightyellow", color(Colors.LIGHTYELLOW))
        .put("lime", color(Colors.LIME))
        .put("limegreen", color(Colors.LIMEGREEN))
        .put("linen", color(Colors.LINEN))
        .put("magenta", color(Colors.MAGENTA))
        .put("maroon", color(Colors.MAROON))
        .put("mediumaquamarine", color(Colors.MEDIUMAQUAMARINE))
        .put("mediumblue", color(Colors.MEDIUMBLUE))
        .put("mediumorchid", color(Colors.MEDIUMORCHID))
        .put("mediumpurple", color(Colors.MEDIUMPURPLE))
        .put("mediumseagreen", color(Colors.MEDIUMSEAGREEN))
        .put("mediumslateblue", color(Colors.MEDIUMSLATEBLUE))
        .put("mediumspringgreen", color(Colors.MEDIUMSPRINGGREEN))
        .put("mediumturquoise", color(Colors.MEDIUMTURQUOISE))
        .put("mediumvioletred", color(Colors.MEDIUMVIOLETRED))
        .put("midnightblue", color(Colors.MIDNIGHTBLUE))
        .put("mintcream", color(Colors.MINTCREAM))
        .put("mistyrose", color(Colors.MISTYROSE))
        .put("moccasin", color(Colors.MOCCASIN))
        .put("navajowhite", color(Colors.NAVAJOWHITE))
        .put("navy", color(Colors.NAVY))
        .put("oldlace", color(Colors.OLDLACE))
        .put("olive", color(Colors.OLIVE))
        .put("olivedrab", color(Colors.OLIVEDRAB))
        .put("orange", color(Colors.ORANGE))
        .put("orangered", color(Colors.ORANGERED))
        .put("orchid", color(Colors.ORCHID))
        .put("palegoldenrod", color(Colors.PALEGOLDENROD))
        .put("palegreen", color(Colors.PALEGREEN))
        .put("paleturquoise", color(Colors.PALETURQUOISE))
        .put("palevioletred", color(Colors.PALEVIOLETRED))
        .put("papayawhip", color(Colors.PAPAYAWHIP))
        .put("peachpuff", color(Colors.PEACHPUFF))
        .put("peru", color(Colors.PERU))
        .put("pink", color(Colors.PINK))
        .put("plum", color(Colors.PLUM))
        .put("powderblue", color(Colors.POWDERBLUE))
        .put("purple", color(Colors.PURPLE))
        .put("rebeccapurple", color(Colors.REBECCAPURPLE))
        .put("red", color(Colors.RED))
        .put("rosybrown", color(Colors.ROSYBROWN))
        .put("royalblue", color(Colors.ROYALBLUE))
        .put("saddlebrown", color(Colors.SADDLEBROWN))
        .put("salmon", color(Colors.SALMON))
        .put("sandybrown", color(Colors.SANDYBROWN))
        .put("seagreen", color(Colors.SEAGREEN))
        .put("seashell", color(Colors.SEASHELL))
        .put("sienna", color(Colors.SIENNA))
        .put("silver", color(Colors.SILVER))
        .put("skyblue", color(Colors.SKYBLUE))
        .put("slateblue", color(Colors.SLATEBLUE))
        .put("slategray", color(Colors.SLATEGRAY))
        .put("slategrey", color(Colors.SLATEGREY))
        .put("snow", color(Colors.SNOW))
        .put("springgreen", color(Colors.SPRINGGREEN))
        .put("steelblue", color(Colors.STEELBLUE))
        .put("tan", color(Colors.TAN))
        .put("teal", color(Colors.TEAL))
        .put("thistle", color(Colors.THISTLE))
        .put("tomato", color(Colors.TOMATO))
        .put("turquoise", color(Colors.TURQUOISE))
        .put("violet", color(Colors.VIOLET))
        .put("wheat", color(Colors.WHEAT))
        .put("white", color(Colors.WHITE))
        .put("whitesmoke", color(Colors.WHITESMOKE))
        .put("yellow", color(Colors.YELLOW))
        .put("yellowgreen", color(Colors.YELLOWGREEN))
        .build();
  }
  
  private static ZwlValue color(Colors c) {
    return new StringZwlValue(c.getColorValue().asRgba());
  }
}