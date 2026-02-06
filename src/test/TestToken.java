package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import token.Token;
import token.TokenType;

public class TestToken {

	@Test
	void testCostruttore1() {
		Token t = new Token(0, TokenType.INT, "3");
		assertEquals(TokenType.INT, t.getTipo());
		assertEquals("3", t.getValore());
		assertEquals(0, t.getRiga());
		assertEquals("<INT, r:0, val:3>", t.toString());
	}

	@Test
	void testCostruttore2() {
		Token t = new Token(1, TokenType.PLUS);
		assertEquals(TokenType.PLUS, t.getTipo());
		assertEquals("<PLUS, r:1>", t.toString());
	}
}
