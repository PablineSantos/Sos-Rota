package com.pi.grafos.view.styles;

import java.io.InputStream;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AppStyles {

    // ==========================================
    // 1. PALETA DE CORES
    // ==========================================

    // Transformando as Strings Hexadec em objetos Color do JavaFX para uso direto
    public static final Color COR_VERMELHO_RESGATE = Color.web("#D92027");
    public static final Color COR_AZUL_NOTURNO     = Color.web("#1E293B");
    public static final Color COR_CINZA_FUNDO      = Color.web("#F1F5F9");
    public static final Color COR_TEXTO_PRETO     = Color.web("#000000");
    public static final Color COR_TEXTO_BRANCO     = Color.web("#ffffff");
    public static final Color COR_TEXTO_CLARO      = Color.web("#666666");

    // Cores de Status
    public static final Color STATUS_ALTA   = Color.web("#EF4444"); // Vermelho
    public static final Color STATUS_MEDIA  = Color.web("#F59E0B"); // Laranja
    public static final Color STATUS_BAIXA  = Color.web("#10B981"); // Verde

    // String Hex (para usar em setStyle "-fx-background-color: ...")
    public static final String HEX_VERMELHO = "#D92027";
    public static final String HEX_AZUL     = "#181368";
    public static final String HEX_CINZA_FUNDO     = "#1E293B";

    // Background Dashboard
    // Cor específica para o Menu Lateral (Cinza Moderno Escuro)
    public static final String HEX_SIDEBAR_BG = "#1E293B"; // Azul Noturno Profundo
    public static final String HEX_SIDEBAR_BTN = "#334155"; // Cor do botão normal
    public static final String HEX_SIDEBAR_HOVER = "#D92027"; // Cor ao passar o mouse (Vermelho Marca)


    // ==========================================
    // 2. FONTES
    // ==========================================

    // Defini constantes para os tamanhos que vamos usar
    public static final Font FONTE_TITULO;    // Ex: "Login", "Dashboard" (40px)
    public static final Font FONTE_SUBTITULO; // Ex: Labels de formulário (20px)
    public static final Font FONTE_CORPO;     // Ex: Texto comum, Inputs (14px)
    public static final Font FONTE_PEQUENA;   // Ex: Copyright, legendas (12px)
    public static final Font FONTE_BOTAO;   //
    public static final Font FONTE_BOTAO2;   //


    // O bloco "static" roda automaticamente assim que o programa começa
    static {
        // Tenta carregar a Poppins. Se falhar, usa Arial como backup.
        FONTE_TITULO    = carregarFonte("Poppins-Bold.ttf", 36);
        FONTE_SUBTITULO = carregarFonte("Poppins-Regular.ttf", 22);
        FONTE_CORPO     = carregarFonte("Poppins-Regular.ttf", 16);
        FONTE_PEQUENA   = carregarFonte("Poppins-Light.ttf", 16);
        FONTE_BOTAO     = carregarFonte("Poppins-Bold.ttf", 24);
        FONTE_BOTAO2     = carregarFonte("Poppins-Regular.ttf", 14);
    }

    /**
     * Método utilitário privado para carregar a fonte sem repetir código try-catch
     */
    private static Font carregarFonte(String nomeArquivo, double tamanho) {
        try {
            String caminho = "/fonts/Poppins/" + nomeArquivo;

            InputStream fontStream = AppStyles.class.getResourceAsStream(caminho);

            if (fontStream != null) {
                return Font.loadFont(fontStream, tamanho);
            } else {
                System.err.println("ALERTA: Arquivo não encontrado no classpath: " + caminho);
                return Font.font("Arial", FontWeight.NORMAL, tamanho); // Fallback
            }
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao carregar fonte: " + e.getMessage());
            return Font.font("Arial", tamanho);
        }
    }
}