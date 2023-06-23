package org.xjl.command;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ArgsTest {
    // -l -p 8080 -d /usr/logs
    // [-l], [-p, 8000], [-d, usr/logs]
    // api构思与组件划分
    // 视频中使用的是record，为了适配java 8进行了改进

    @Test
    public void should_example1() {
        MultiOptions options = Args.parse(MultiOptions.class, "-l", "-p", "8080", "-d", "usr/logs");
        assertTrue(options.getLogging());
        assertEquals(options.getPort(), 8080);
        assertEquals(options.getDirectory(), "usr/logs");
    }

    // 功能分解与任务列表
    // single option
    // multi option
    // sad path
    // default value

    @Test
    void should_set_boolean_option_to_return_true_if_flag_present(){
        BooleanOption option = Args.parse(BooleanOption.class, "-l");
        assertTrue(option.getLogging());
    }

    @Test
    void should_set_boolean_option_to_return_false_if_flag_not_present(){
        BooleanOption option = Args.parse(BooleanOption.class);
        assertFalse(option.getLogging());
    }


    @Test
    void should_parse_int_as_option_value() {
        IntOption option = Args.parse(IntOption.class, "-p", "8080");
        assertEquals(8080, option.getPort());
    }


    @Test
    void should_parse_string_as_option_value() {
        StringOption option = Args.parse(StringOption.class, "-d", "/usr/logs");
        assertEquals("/usr/logs", option.getDirectory());
    }

    @Test
    void should_parse_multi_options(){
        MultiOptions options = Args.parse(MultiOptions.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.getLogging());
        assertEquals(8080, options.getPort());
        assertEquals("/usr/logs", options.getDirectory());
    }

}
