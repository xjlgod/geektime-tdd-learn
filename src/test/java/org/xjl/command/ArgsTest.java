package org.xjl.command;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void should_parse_multi_options(){
        MultiOptions options = Args.parse(MultiOptions.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.getLogging());
        assertEquals(8080, options.getPort());
        assertEquals("/usr/logs", options.getDirectory());
    }

    @Test
    public void should_throw_illegal_option_exception_if_annotation_not_present() {
        IllegalOptionException exception = assertThrows(IllegalOptionException.class, () -> {
            Args.parse(OptionsWithOutAnnotation.class, "-l", "-p", "8080", "-d", "/usr/logs");
        });
        assertEquals("arg1", exception.getParameter());
    }
}
