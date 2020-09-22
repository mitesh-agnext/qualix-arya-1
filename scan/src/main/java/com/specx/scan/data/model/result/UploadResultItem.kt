package com.specx.scan.data.model.result

class UploadResultItem(val protein: String?, val moisture: String?, val curcumin: String?,
                       val oil: String?, val fat: String?, val snf: String?, val urea: String?,
                       val detergent: String?, val glabridin: String?, val palm_oil: String?,
                       val one_banjhi_count: String?, val one_bud_count: String?,
                       val one_leaf_banjhi: String?, val one_leaf_bud: String?,
                       val one_leaf_count: String?, val quality_score: String?,
                       val three_leaf_bud: String?, val three_leaf_count: String?,
                       val two_leaf_banjhi: String?, val two_leaf_bud: String?,
                       val two_leaf_count: String?, val total_count: String?,
                       val grain_count: String?, val count_per_oz: String?,
                       val aspect_ratio: String?, val radius: String?,
                       val clean: String?, val weevilled: String?,
                       val immature: String?, val shrivelled: String?,
                       val broken: String?, val damaged: String?,
                       val discolored: String?, val admixture: String?,
                       val foreign_matters: String?, val red_rice: String?,
                       val chalky: String?, val black: String?, val brown: String?,
                       val green: String?, val other: String?, val shell: String?,
                       val density: String?, val karnal_bunt: String?, val potiya: String?) {

    class Builder {

        private var protein: String? = null
        private var moisture: String? = null
        private var curcumin: String? = null
        private var oil: String? = null
        private var fat: String? = null
        private var snf: String? = null
        private var urea: String? = null
        private var detergent: String? = null
        private var glabridin: String? = null
        private var palm_oil: String? = null

        private val one_banjhi_count: String? = null
        private val one_bud_count: String? = null
        private val one_leaf_banjhi: String? = null
        private val one_leaf_bud: String? = null
        private val one_leaf_count: String? = null
        private val quality_score: String? = null
        private val three_leaf_bud: String? = null
        private val three_leaf_count: String? = null
        private val two_leaf_banjhi: String? = null
        private val two_leaf_bud: String? = null
        private val two_leaf_count: String? = null
        private val total_count: String? = null

        private val grain_count: String? = null
        private val count_per_oz: String? = null
        private val aspect_ratio: String? = null
        private val radius: String? = null
        private val clean: String? = null
        private val weevilled: String? = null
        private val immature: String? = null
        private val shrivelled: String? = null
        private val broken: String? = null
        private val damaged: String? = null
        private val discolored: String? = null
        private val admixture: String? = null
        private val foreign_matters: String? = null
        private val red_rice: String? = null
        private val chalky: String? = null
        private val black: String? = null
        private val brown: String? = null
        private val green: String? = null
        private val other: String? = null
        private val shell: String? = null
        private val density: String? = null
        private val karnal_bunt: String? = null
        private val potiya: String? = null

        fun setProtein(protein: String?): Builder {
            this.protein = protein
            return this
        }

        fun setMoisture(moisture: String?): Builder {
            this.moisture = moisture
            return this
        }

        fun setCurcumin(curcumin: String?): Builder {
            this.curcumin = curcumin
            return this
        }

        fun setOil(oil: String?): Builder {
            this.oil = oil
            return this
        }

        fun setFat(fat: String?): Builder {
            this.fat = fat
            return this
        }

        fun setSnf(snf: String?): Builder {
            this.snf = snf
            return this
        }

        fun setUrea(urea: String?): Builder {
            this.urea = urea
            return this
        }

        fun setDetergent(detergent: String?): Builder {
            this.detergent = detergent
            return this
        }

        fun setGlabridin(glabridin: String?): Builder {
            this.glabridin = glabridin
            return this
        }

        fun setPalmOil(palm_oil: String?): Builder {
            this.palm_oil = palm_oil
            return this
        }

        fun build(): UploadResultItem {
            return UploadResultItem(protein, moisture, curcumin, oil, fat, snf, urea, detergent,
                    glabridin, palm_oil, one_banjhi_count, one_bud_count, one_leaf_banjhi, one_leaf_bud,
                    one_leaf_count, quality_score, three_leaf_bud, three_leaf_count, two_leaf_banjhi,
                    two_leaf_bud, two_leaf_count, total_count, grain_count, count_per_oz,
                    aspect_ratio, radius, clean, weevilled, immature, shrivelled,
                    broken, damaged, discolored, admixture, foreign_matters, red_rice,
                    chalky, black, brown, green, other, shell, density, karnal_bunt, potiya
            )
        }
    }
}