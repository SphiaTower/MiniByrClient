/**
 *
 */
package com.example.qingunext.app.page_thread;

import java.util.*;

/**
 * UBB解码类
 *
 * @author liudong
 */
public class UBBDecoder {

    public static final int MODE_IGNORE = 0;
    public static final int MODE_CLOSE = 1;

    private static final int SEARCH_LEN = 200;

    /**
     * 进行UBB标签的转换
     *
     * @param s    需要转换的包含UBB标签的文本
     * @param th   用户自定义的UBB标签的处理器的实例
     * @param mode 容错模式，可以是忽略模式(MODE_IGNORE)或关闭模式(MODE_CLOSE)
     * @return 转换后的包含HTML标签的文本
     */
    public static String decode(String s, UBBTagHandler th, int mode) {
        return decode(s, th, mode, false);
    }

    public static String decode(String s) {
        return UBBDecoder.decode(s, new SimpleTagHandler(),
                UBBDecoder.MODE_CLOSE);
    }

    /**
     * 进行UBB标签的转换
     *
     * @param s      需要转换的包含UBB标签的文本
     * @param th     用户自定义的UBB标签的处理器的实例
     * @param mode   容错模式，可以是忽略模式(MODE_IGNORE)或关闭模式(MODE_CLOSE)
     * @param convertBr 是否把'\n'字符也转换为'<br>'
     * @return 转换后的包含HTML标签的文本
     */
    public static String decode(String s, UBBTagHandler th, int mode,
                                boolean convertBr) {
        StringBuffer buf = new StringBuffer(); // 当前文本
        char[] cc = s.toCharArray(); // 把输入转换为字符数组以提高处理性能
        int len = cc.length, pos = 0;
        UBBNode root = new UBBNode(null, "", null, "", false); // 根节点
        UBBNode node = root; // 当前节点
        Stack<UBBNode> stk = new Stack<UBBNode>(); // 使用堆栈处理节点的嵌套
        stk.push(node);
        while (pos < len) { // 只要未到文件末尾就循环
            char cur = cc[pos]; // 当前字符
            if (convertBr && cur == '\n') { // 如果当前字符是换行
                buf.append("\n<br />");
                pos++;
                continue;
            }
            if (convertBr && cur == '\t') { // 如果当前字符是制表符
                buf.append("&nbsp;&nbsp;&nbsp;&nbsp;");
                pos++;
                continue;
            }
            if (convertBr && cur == ' ') { // 如果当前字符是空字符
                buf.append("&nbsp;");
                pos++;
                continue;
            }
            if (cur != '[') { // 只要当前字符不是'['就扩展到当前文本
                buf.append(cur);
                pos++;
                continue;
            }
            // 如果当前字符是'['，那么查找下一个']'
            int ii = indexOf(cc, ']', pos + 1, SEARCH_LEN);
            if (ii == -1) { // 未找到，把当前'['作为一般字符处理，扩展到当前文本
                buf.append(cur);
                pos++;
                continue;
            }
            if (cc[pos + 1] == '/') { // 标签以'/'起始，可能是结束标签
                if (cc[pos + 2] == ']') { // 修正littlebat发现的bug，处理"[/]"的情况
                    buf.append("[/]");
                    pos += 3;
                    continue;
                }
                // 得到结束标签的文本
                String tmp = new String(cc, pos + 2, ii - pos - 2).trim()
                        .toLowerCase();
                int cnt = 1;
                boolean find = false;
                // 在节点树上向上查找和本结束标签对应的标签
                for (UBBNode nd = node; nd != null; nd = nd.parent, cnt++) {
                    if (nd.tag.equals(tmp)) {
                        find = true;
                        break;
                    }
                }
                if (find) { // 如果在节点树上找到了和本结束标签对应的标签
                    // 先把当前文本扩展到当前节点
                    addTextChild(node, buf);
                    // 从堆栈中弹出节点，直到找到的标签成为当前节点
                    while (cnt-- > 0) {
                        // 对于CLOSE容错模式，把当前节点和找到的标签节点之间的标签全部关闭
                        if (mode == MODE_CLOSE) {
                            node.closed = true;
                        }
                        node = stk.pop();
                    }
                    // 关闭当前标签节点，当前节点上移一层
                    node.closed = true;
                    node = node.parent;
                    pos = ii + 1;
                    continue;
                } else { // 未找到对应起始标签，作为普通文本处理
                    buf.append("[/");
                    pos += 2;
                    continue;
                }
            } else if (cc[ii - 1] == '/') { // 标签以'/'结尾，可能是空标签
                String tmp = new String(cc, pos + 1, ii - pos - 2).trim();
                // 由UBBTagHandler决定是否是一个合法空标签
                String[] ss = th.parseTag(tmp, true);
                if (ss != null && ss.length == 3) { // 处理空标签
                    // 先把当前文本扩展到当前节点
                    addTextChild(node, buf);
                    UBBNode nd = new UBBNode(node, ss[0].toLowerCase(), ss[1]
                            .split(","), new String(cc, pos, ii + 1 - pos),
                            true);
                    node.addChild(nd);
                    pos = ii + 1;
                    continue;
                }
            }
            // 可能是普通起始标签
            // 得到标签文本
            String tmp = new String(cc, pos + 1, ii - pos - 1).trim();
            // 由UBBTagHandler决定是否是合法标签
            String[] ss = th.parseTag(tmp, false);
            if (ss != null && ss.length == 2) { // 是合法标签
                // 先把当前文本扩展到当前节点
                addTextChild(node, buf);
                // 创建新的节点，扩展到当前节点，然后当前节点下移一层
                UBBNode nd = new UBBNode(node, ss[0].toLowerCase(), ss[1]
                        .split(","), new String(cc, pos, ii + 1 - pos), false);
                node.addChild(nd);
                pos = ii + 1;
                stk.push(nd);
                node = nd;
            } else { // 不是标签，当作普通文本处理
                buf.append('[');
                pos++;
            }
        }
        // 把当前文本中剩余的内容扩展到当前节点
        addTextChild(node, buf);
        //System.out.println("=========================\n" + root.toString(0));
        // 使用节点树构造输出文本
        String show = decodeNode(th, root);
        show = show.replaceAll("\\[br\\]", "\n<br />");
        return show.replaceAll("\\[em([a-zA-Z]?)(\\d+?)\\]",  // .匹配单个字符
                "<img src='http://bbs.byr.cn/img/ubb/em$1/$2.gif' style=\"border:0\">"); // $代表第几个匹配结果
    }

    /**
     * 把文本生成一个纯文本节点并扩展到给定的节点
     *
     * @param node
     * @param buf
     */
    private static void addTextChild(UBBNode node, StringBuffer buf) {
        if (buf.length() > 0) {
            node.addChild(new UBBNode(node, buf.toString()));
            buf.setLength(0);
        }
    }

    /**
     * 从标签节点树来递归的构造输出文本
     *
     * @param th
     * @param node
     * @return
     */
    private static String decodeNode(UBBTagHandler th, UBBNode node) {
        StringBuilder buf = new StringBuilder(); // 输出文本
        if (UBBNode.TEXT.equals(node.tag)) {
            // 处理纯文本节点
            buf.append(node.img);
        } else if (!node.closed) {
            // 处理未正常关闭的节点，节点本身当作纯文本处理，对其子节点进行递归处理
            buf.append(node.img);
            List<UBBNode> lst = node.children;
            if (lst != null && lst.size() > 0) {
                for (UBBNode aLst : lst) {
                    buf.append(decodeNode(th, aLst));
                }
            }
        } else {
            // 处理正常节点，使用UBBTagHandler来组合输出，并递归处理其子节点
            List<UBBNode> lst = node.children;
            StringBuilder tmp = new StringBuilder();
            if (lst != null && lst.size() > 0) {
                for (UBBNode aLst : lst) {
                    tmp.append(decodeNode(th, aLst));
                }
            }
            buf.append(th.compose(node.tag, node.attr, tmp.toString(),
                    node.isEmpty));
        }
        return buf.toString();
    }

    private static int indexOf(char[] cc, char c, int idx, int len) {
        int end = idx + len;
        if (end > cc.length)
            end = cc.length;
        for (int i = idx; i < end; i++) {
            if (cc[i] == c) {
                return i;
            }
        }
        return -1;
    }

}

