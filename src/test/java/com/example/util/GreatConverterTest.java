package com.example.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class GreatConverterTest {

    /**
     * Проверят работу обсервера, на корректность обработки слушателей
     */
    @Test
    public void testConverter() {
        assertEquals("Gold", GreateConverter.convertString("anything"));
    }

}