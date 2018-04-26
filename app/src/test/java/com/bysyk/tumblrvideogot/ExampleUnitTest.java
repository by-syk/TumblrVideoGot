package com.bysyk.tumblrvideogot;

import com.bysyk.tumblrvideogot.util.Utils;

import org.junit.Test;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        Pattern pattern = Pattern.compile("^.+?/tumblr_([\\da-zA-Z]+)(/\\d+)?$");
//        Matcher matcher = pattern.matcher("https://ppgf.tumblr.com/video_file/t:eCbel-heydYNt49YMXk2vA/173079837638/tumblr_p7e3f63wyu1xp9wa3");
//        assertTrue(matcher.matches());
//        assertEquals(matcher.group(1), "p7e3f63wyu1xp9wa3");
//        assertNull(matcher.group(2));
//        pattern = Pattern.compile("^.+?/tumblr_([\\da-zA-Z]+)(_\\d+)?(\\..+)$");
//        matcher = pattern.matcher("https://vt.media.tumblr.com/tumblr_p4c7qbtemI1wrhoyi.mp4");
//        assertTrue(matcher.matches());
//        assertEquals(matcher.group(1), "p4c7qbtemI1wrhoyi");
//        assertNull(matcher.group(2));
//        assertEquals(matcher.group(3), ".mp4");
//        assertEquals("https://ppgf.tumblr.com/video_file/t:eCbel-heydYNt49YMXk2vA/173079837638/tumblr_p7e3f63wyu1xp9wa3/480"
//                .replaceAll("/480$", ""),
//                "https://ppgf.tumblr.com/video_file/t:eCbel-heydYNt49YMXk2vA/173079837638/tumblr_p7e3f63wyu1xp9wa3");
//        assertEquals("https://vt.media.tumblr.com/tumblr_p4c7qbtemI1wrhoyi_480.mp4"
//                        .replaceAll("_480.mp4$", ""),
//                "https://vt.media.tumblr.com/tumblr_p4c7qbtemI1wrhoyi");
//        assertNull(EscapeUtils.unescape("\\u4f60"));
        assertNull(Utils.parseEscapedHans("{\"status\":\"error\",\"message\":\"\\u53c2\\u6570\\u6821\\u68c0\\u5931\\u8d25\\uff0c\\u8bf7<span class='text-danger'>\\u5237\\u65b0\\u7f51\\u9875\\u540e\\u91cd\\u65b0\\u63d0\\u4ea4\\u89e3\\u6790<\\/span>\\uff01\"}"));
    }
}