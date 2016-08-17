package com.doing.library.core.io;

import com.doing.library.core.utils.StringUtils;

public class SafeFileNameGenerator implements NameGenerator {

    @Override
    public String generate(String key) {
        return StringUtils.toSafeFileName(key);
    }

}
