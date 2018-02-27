/**
 * Copyright DingXuan. 2017 All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.node.util;

import org.apache.commons.cli.*;

import java.util.List;

/**
 * 完成Peer节点的解析工作
 *
 * @author zhouhui
 * @date 2018/2/23
 * @company Dingxuan
 */
public class NodeCLIParser implements CommandLineParser {

    /** The command-line instance. */
    private NodeCLI cmd;

    /** The current options. */
    private Options options;

    /**
     * Flag indicating how unrecognized tokens are handled. <tt>true</tt> to stop
     * the parsing and add the remaining tokens to the args list.
     * 标记，用于指示如何去处理未识别的令牌。如果值为true表停止解析并将剩余的令牌加入arg列表中
     * <tt>false</tt> to throw an exception.
     */
    private boolean stopAtNonOption;

    /** The last option parsed. */
    private Option currentOption;

    /**
     * Flag indicating if tokens should no longer be analyzed and simply added as arguments of the command line.
     * 标记，用于指示如果令牌不再被分析，那么将其简单添加作为命令行的参数
     */
    private boolean skipParsing;

    /**
     * 是否找到对应的选项
     */
    private boolean findOption;


    @Override
    public CommandLine parse(Options options, String[] arguments) throws ParseException {
        return parse(options, arguments, false);
    }

    @Override
    public CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException {
        this.options = options;
        this.stopAtNonOption = stopAtNonOption;

        //重置中间变量
        skipParsing = false;
        currentOption = null;
        findOption = false;

        cmd = new NodeCLI();
        if (arguments != null) {
            for (String argument : arguments) {
                handleToken(argument);
            }
        }

        // check the arguments of the last option
        //由于在for循环体中未检查完最后一个option，所以增加对最后一个option的检查
        checkRequiredArgs(currentOption);

        return cmd;
    }

    /**
     * Handle any command line token.
     *
     * @param token the command line token to handle
     * @throws ParseException
     */
    private void handleToken(String token) throws ParseException
    {
        if (skipParsing)
        {
            cmd.getArgList().add(token);
        }
        else if ("--".equals(token))
        {
            skipParsing = true;
        }
        else if (currentOption != null && acceptsArg(currentOption) && isArgument(token))
        {
            addValueForProcessing(currentOption, stripLeadingAndTrailingQuotes(token));
        }
        else if (token.startsWith("--"))
        {
            handleLongOption(token);
        }
        else if (token.startsWith("-") && !"-".equals(token))
        {
            handleShortAndLongOption(token);
        }
        else
        {
            if(!findOption){
                handleShortAndLongOption(token);
            }else {
                handleUnknownToken(token);
            }
        }

        if (currentOption != null && !acceptsArg(currentOption))
        {
            currentOption = null;
        }
    }

    /**
     * Adds the specified value to this Option.
     *
     * @param value is a/the value of this Option
     */
    void addValueForProcessing(Option option, String value)
    {
        if (option.getArgs() == Option.UNINITIALIZED)
        {
            throw new RuntimeException("NO_ARGS_ALLOWED");
        }
        processValue(option, value);
    }

    /**
     * Processes the value.  If this Option has a value separator
     * the value will have to be parsed into individual tokens.  When
     * n-1 tokens have been processed and there are more value separators
     * in the value, parsing is ceased and the remaining characters are
     * added as a single token.
     *
     * @param value The String to be processed.
     *
     * @since 1.0.1
     */
    private void processValue(Option option, String value)
    {
        // this Option has a separator character
        if (option.hasValueSeparator())
        {
            // get the separator character
            char sep = option.getValueSeparator();

            // store the index for the value separator
            int index = value.indexOf(sep);

            // while there are more value separators
            while (index != -1)
            {
                // next value to be added
                if (option.getValuesList().size() == option.getArgs() - 1)
                {
                    break;
                }

                // store
                option.getValuesList().add(value.substring(0, index));

                // parse
                value = value.substring(index + 1);

                // get new index
                index = value.indexOf(sep);
            }
        }

        // store the actual value or the last value that has been parsed
        option.getValuesList().add(value);
    }

    /**
     * Remove the leading and trailing quotes from <code>str</code>.
     * 为str移除前导和尾随的引号
     * E.g. if str is '"one two"', then 'one two' is returned.
     *
     * @param str The string from which the leading and trailing quotes
     * should be removed.
     *
     * @return The string without the leading and trailing quotes.
     */
    private String stripLeadingAndTrailingQuotes(String str)
    {
        int length = str.length();
        if (length > 1 && str.startsWith("\"") && str.endsWith("\"") && str.substring(1, length - 1).indexOf('"') == -1)
        {
            str = str.substring(1, length - 1);
        }

        return str;
    }

    /**
     * Returns true is the token is a valid argument.
     *
     * @param token
     */
    private boolean isArgument(String token)
    {
        return !isOption(token) || isNegativeNumber(token);
    }

    /**
     * Check if the token is a negative number.
     *
     * @param token
     */
    private boolean isNegativeNumber(String token)
    {
        try
        {
            Double.parseDouble(token);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * Tells if the token looks like an option.
     *
     * @param token
     */
    private boolean isOption(String token)
    {
        return isLongOption(token) || isShortOption(token);
    }

    /**
     * Tells if the token looks like a short option.
     *
     * @param token
     */
    private boolean isShortOption(String token)
    {
        // short options (-S, -SV, -S=V, -SV1=V2, -S1S2)
        if (!token.startsWith("-") || token.length() == 1)
        {
            return false;
        }

        // remove leading "-" and "=value"
        int pos = token.indexOf("=");
        String optName = pos == -1 ? token.substring(1) : token.substring(1, pos);
        if (options.hasShortOption(optName))
        {
            return true;
        }
        // check for several concatenated short options
        return optName.length() > 0 && options.hasShortOption(String.valueOf(optName.charAt(0)));
    }

    /**
     * Tells if the token looks like a long option.
     *
     * @param token
     */
    private boolean isLongOption(String token)
    {
        if (!token.startsWith("-") || token.length() == 1)
        {
            return false;
        }

        int pos = token.indexOf("=");
        String t = pos == -1 ? token : token.substring(0, pos);

        if (!options.getMatchingOptions(t).isEmpty())
        {
            // long or partial long options (--L, -L, --L=V, -L=V, --l, --l=V)
            return true;
        }
        else if (getLongPrefix(token) != null && !token.startsWith("--"))
        {
            // -LV
            return true;
        }

        return false;
    }

    /**
     * Search for a prefix that is the long name of an option (-Xmx512m)
     *
     * @param token
     */
    private String getLongPrefix(String token)
    {
        String t = stripLeadingHyphens(token);

        int i;
        String opt = null;
        for (i = t.length() - 2; i > 1; i--)
        {
            String prefix = t.substring(0, i);
            if (options.hasLongOption(prefix))
            {
                opt = prefix;
                break;
            }
        }

        return opt;
    }

    /**
     * Remove the hyphens from the beginning of <code>str</code> and
     * return the new String.
     *
     * @param str The string from which the hyphens should be removed.
     *
     * @return the new String.
     */
    private String stripLeadingHyphens(String str)
    {
        if (str == null)
        {
            return null;
        }
        if (str.startsWith("--"))
        {
            return str.substring(2, str.length());
        }
        else if (str.startsWith("-"))
        {
            return str.substring(1, str.length());
        }

        return str;
    }

    /**
     * Handles the following tokens:
     *
     * --L
     * --L V
     * --l
     *
     * @param token the command line token to handle
     */
    private void handleLongOption(String token) throws ParseException
    {
        List<String> matchingOpts = options.getMatchingOptions(token);
        if (matchingOpts.isEmpty())
        {
            handleUnknownToken(token);
        }
        else if (matchingOpts.size() > 1)
        {
            //匹配到兩個以上
            throw new AmbiguousOptionException(token, matchingOpts);
        }
        else
        {
            handleOption(options.getOption(matchingOpts.get(0)));
        }
    }

    /**
     * Handles the following tokens:
     *
     * -S
     * -SV
     * -S V
     * -S=V
     * -S1S2
     * -S1S2 V
     * -SV1=V2
     *
     * -L
     * -LV
     * -L V
     * -L=V
     * -l
     *
     * @param token the command line token to handle
     */
    private void handleShortAndLongOption(String token) throws ParseException
    {
        String t = stripLeadingHyphens(token);

        if (t.length() == 1)
        {//單字母
            // -S
            if (options.hasShortOption(t))
            {
                handleOption(options.getOption(t));
            }
            else
            {
                handleUnknownToken(token);
            }
        }
        else
        {//非等號
            // no equal sign found (-xxx)
            if (options.hasShortOption(t))
            {
                handleOption(options.getOption(t));
            }
            else if (!options.getMatchingOptions(t).isEmpty())
            {
                // -L or -l
                handleLongOption(token);
            }
            else
            {
                handleUnknownToken(token);
            }
        }
    }

    /**
     * Handles an unknown token. If the token starts with a dash an
     * UnrecognizedOptionException is thrown. Otherwise the token is added
     * to the arguments of the command line. If the stopAtNonOption flag
     * is set, this stops the parsing and the remaining tokens are added
     * as-is in the arguments of the command line.
     *
     * @param token the command line token to handle
     */
    private void handleUnknownToken(String token) throws ParseException
    {
        cmd.addArg(token);
        if (stopAtNonOption)
        {
            skipParsing = true;
        }
    }

    private void handleOption(Option option) throws ParseException
    {
        // check the previous option before handling the next one
        checkRequiredArgs(option);

        option = (Option) option.clone();
        cmd.addOption(option);

        if (option.hasArg())
        {
            currentOption = option;
            findOption = true;
        }
        else
        {
            currentOption = null;
        }
    }

    /**
     * Returns the 'unique' Option identifier.
     *
     * @return the 'unique' Option identifier
     */
    private String getKey(Option option)
    {
        // if 'opt' is null, then it is a 'long' option
        return (option.getOpt() == null) ? option.getLongOpt() : option.getOpt();
    }

    /**
     * Throw a {@link MissingArgumentException} if the current option
     * didn't receive the number of arguments expected.
     */
    private void checkRequiredArgs(Option option) throws ParseException
    {
        if (option != null && requiresArg(option))
        {
            throw new MissingArgumentException(option);
        }
    }

    /**
     * Tells if the option requires more arguments to be valid.
     * 说明该option需要更多的参数才能可用
     *
     * @return false if the option doesn't require more arguments
     */
    private boolean requiresArg(Option option)
    {
        if (option.hasOptionalArg())
        {
            return false;
        }

        if (option.getArgs() == Option.UNLIMITED_VALUES)
        {//如果不限制参数，只要保证至少有一个即可
            return option.getValuesList().isEmpty();
        }

        //如果限制参数,保证可以接受参数
        return acceptsArg(option);
    }

    /**
     * Tells if the option can accept more arguments.
     * 说明是否该option可以接受更多的参数
     *
     * @return false if the maximum number of arguments is reached 如果达到了参数的最大值返回false
     */
    private boolean acceptsArg(Option option)
    {
        //第一个判定条件：是否需要参数，或是否有可选参数
        //第二个判定条件：是否不指定数量/无穷多，或实际值未到达该option能包含的最大值
        return (option.hasArg() || option.hasOptionalArg())
                && (option.getArgs() <= 0 || option.getValuesList().size() < option.getArgs());
    }
}
