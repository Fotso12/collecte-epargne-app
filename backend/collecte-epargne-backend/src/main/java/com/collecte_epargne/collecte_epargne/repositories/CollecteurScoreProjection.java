package com.collecte_epargne.collecte_epargne.repositories;

import java.math.BigDecimal;

public interface CollecteurScoreProjection {
    Integer getIdEmploye();
    String getMatricule();
    String getNom();
    String getPrenom();
    BigDecimal getTotalScore();
}
