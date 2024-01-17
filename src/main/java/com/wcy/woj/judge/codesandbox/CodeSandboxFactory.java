package com.wcy.woj.judge.codesandbox;

import com.wcy.woj.constant.CodeSandboxConstant;
import com. wcy.woj.judge.codesandbox.impl.ExampleCodeSandbox;
import com. wcy.woj.judge.codesandbox.impl.RemoteCodeSandbox;
import com. wcy.woj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     */
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case CodeSandboxConstant.CODE_SANDBOX_TYPE_REMOTE:
                return new RemoteCodeSandbox();
            case CodeSandboxConstant.CODE_SANDBOX_TYPE_THIRD_PARTY:
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
