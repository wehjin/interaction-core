package com.rubyhuntersky.data.report

sealed class ReportConclusion {

    object AddConstituent : ReportConclusion()
    object RefreshPrices : ReportConclusion()
    data class Divest(val constituentCorrections: List<ConstituentCorrection>) : ReportConclusion()
    data class Maintain(val constituentCorrections: List<ConstituentCorrection>) : ReportConclusion()
}