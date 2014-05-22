package com.dinglabs.culturalinterviews.csv

/**
 * The fields from a CSV file.
 */
case class CsvEntry(
                     number:      Int,
                     section:     String,
                     subtitle:    String,
                     youtubeId:   String,
                     code:        String,
                     location:    String,
                     speaker:     String,
                     english:     String,
                     chineseSimp: String,
                     chineseTrad: String,
                     pinyin:      String)
