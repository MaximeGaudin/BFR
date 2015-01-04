package com.mgaudin.bfi;

import lombok.extern.slf4j.Slf4j;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import com.mgaudin.bfi.BrainfuckLexer;
import com.mgaudin.bfi.BrainfuckParser;

@Slf4j
public class BFI {
    private static final int MEMORY_SIZE = 2048 * 30;
    private static char[] memory = new char[MEMORY_SIZE];
    private static int memoryPointer = 0;

    public static void main(String[] args) throws RecognitionException, IOException {
        String sourceFilename = "fibonacci.bf";
        InputStream resourceAsStream = BFI.class.getClassLoader().getResourceAsStream(sourceFilename);
        ANTLRStringStream in = new ANTLRStringStream(IOUtils.toString(resourceAsStream));

        BrainfuckLexer lexer = new BrainfuckLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        BrainfuckParser parser = new BrainfuckParser(tokens);
        traverseTree((CommonTree) parser.program().getTree());
    }

    private static void traverseTree(CommonTree tree) {
        if (tree.getToken() != null) {
            handleToken(tree.getToken(), tree);
        }

        if (tree.getChildCount() != 0) {
            if (tree.getToken() != null && tree.getToken().getType() == BrainfuckLexer.LOOP_OPEN) {
                // Loop children has already been traversed
                return;
            }

            for (Object child : tree.getChildren()) {
                traverseTree((CommonTree) child);
            }
        }
    }

    private static void handleToken(Token tokenCode, CommonTree tree) {
        switch (tokenCode.getType()) {
            case BrainfuckLexer.INC:
                log.info("+");
                memory[memoryPointer] += 1;
                break;

            case BrainfuckLexer.DEC:
                log.info("-");
                memory[memoryPointer] -= 1;
                break;

            case BrainfuckLexer.DEC_DP:
                log.info("<");
                memoryPointer -= 1;
                break;

            case BrainfuckLexer.INC_DP:
                log.info(">");
                memoryPointer += 1;
                break;

            case BrainfuckLexer.LOOP_OPEN:
                while (memory[memoryPointer] != 0) {
                    if (tree.getChildCount() != 0) {
                        for (Object child : tree.getChildren()) {
                            traverseTree((CommonTree) child);
                        }
                    }
                }
                break;

            case BrainfuckLexer.READ:
                log.info(",");

                try {
                    char readChar = (char) System.in.read();
                    memory[memoryPointer] = readChar;
                } catch (IOException e) {
                    log.warn("Impossible to read from console", e);
                }

                break;

            case BrainfuckLexer.PRINT:
                log.info(".");
                System.out.print(memory[memoryPointer]);
                break;

            default:
                break;
        }
    }
}
