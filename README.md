To build the project: mvn package

To run the code: java -jar target/InformationRetrieval-1.0-SNAPSHOT.jar

To get trec_eval results: 
- Foremost, ensure that the trec_eval tool executable is ready for scoring by running the trec_eval command in the trec_eval-9.0.7 folder. If it is, skip to the third step
- If the trec_eval executable throws an error, make sure to run the "make" command in the trec_eval-9.0.7 folder.
- Run the following code in the trec_eval-9.0.7 folder terminal: trec_eval ../qrels.assignment2.part1 ../queryResults
- This will give the scoring of the current choice of the analyzer and similarity type.

Download GloVe word embedding vectors from here: https://nlp.stanford.edu/projects/glove/

BM25Similarity and EnglishAnalyzer
Trec eval results:

runid                   all     org.apache.lucene.analysis.en.EnglishAnalyzer
num_q                   all     25
num_ret                 all     24085
num_rel                 all     2132
num_rel_ret             all     1174
map                     all     0.2079
gm_map                  all     0.1421
Rprec                   all     0.2687
bpref                   all     0.2442
recip_rank              all     0.7278
iprec_at_recall_0.00    all     0.7557
iprec_at_recall_0.10    all     0.5128
iprec_at_recall_0.20    all     0.3536
iprec_at_recall_0.30    all     0.2789
iprec_at_recall_0.40    all     0.2136
iprec_at_recall_0.50    all     0.1774
iprec_at_recall_0.60    all     0.1082
iprec_at_recall_0.70    all     0.0721
iprec_at_recall_0.80    all     0.0547
iprec_at_recall_0.90    all     0.0336
iprec_at_recall_1.00    all     0.0129
P_5                     all     0.4800
P_10                    all     0.4680
P_15                    all     0.4267
P_20                    all     0.4040
P_30                    all     0.3373
P_100                   all     0.1828
P_200                   all     0.1280
P_500                   all     0.0763
P_1000                  all     0.0470

BM25Similarity and EnglishAnalyzer
Trec eval results(without query expansion):

runid                   all     org.apache.lucene.analysis.en.EnglishAnalyzer
num_q                   all     25
num_ret                 all     24085
num_rel                 all     2132
num_rel_ret             all     1211
map                     all     0.2221
gm_map                  all     0.1600
Rprec                   all     0.2873
bpref                   all     0.2583
recip_rank              all     0.7574
iprec_at_recall_0.00    all     0.7878
iprec_at_recall_0.10    all     0.5444
iprec_at_recall_0.20    all     0.3845
iprec_at_recall_0.30    all     0.3046
iprec_at_recall_0.40    all     0.2322
iprec_at_recall_0.50    all     0.1818
iprec_at_recall_0.60    all     0.1103
iprec_at_recall_0.70    all     0.0739
iprec_at_recall_0.80    all     0.0559
iprec_at_recall_0.90    all     0.0389
iprec_at_recall_1.00    all     0.0134
P_5                     all     0.5040
P_10                    all     0.4880
P_15                    all     0.4427
P_20                    all     0.4200
P_30                    all     0.3427
P_100                   all     0.2008
P_200                   all     0.1404
P_500                   all     0.0793
P_1000                  all     0.0484



BM25Similarity and CustomAnalyzer
Trec eval results(with query expansion):
runid                   all     com.example.CustomAnalyzer
num_q                   all     25
num_ret                 all     24085
num_rel                 all     2132
num_rel_ret             all     1201
map                     all     0.2166
gm_map                  all     0.1526
Rprec                   all     0.2817
bpref                   all     0.2543
recip_rank              all     0.7148
iprec_at_recall_0.00    all     0.7558
iprec_at_recall_0.10    all     0.5304
iprec_at_recall_0.20    all     0.3723
iprec_at_recall_0.30    all     0.3071
iprec_at_recall_0.40    all     0.2359
iprec_at_recall_0.50    all     0.1824
iprec_at_recall_0.60    all     0.1173
iprec_at_recall_0.70    all     0.0746
iprec_at_recall_0.80    all     0.0572
iprec_at_recall_0.90    all     0.0377
iprec_at_recall_1.00    all     0.0130
P_5                     all     0.4960
P_10                    all     0.4720
P_15                    all     0.4400
P_20                    all     0.4100
P_30                    all     0.3440
P_100                   all     0.2004
P_200                   all     0.1386
P_500                   all     0.0775
P_1000                  all     0.0480

BM25Similarity and CustomAnalyzer
Trec eval results(without query expansion):
runid                   all     com.example.CustomAnalyzer
num_q                   all     25
num_ret                 all     24085
num_rel                 all     2132
num_rel_ret             all     1203
map                     all     0.2165
gm_map                  all     0.1527
Rprec                   all     0.2823
bpref                   all     0.2548
recip_rank              all     0.7148
iprec_at_recall_0.00    all     0.7558
iprec_at_recall_0.10    all     0.5304
iprec_at_recall_0.20    all     0.3724
iprec_at_recall_0.30    all     0.3072
iprec_at_recall_0.40    all     0.2380
iprec_at_recall_0.50    all     0.1831
iprec_at_recall_0.60    all     0.1177
iprec_at_recall_0.70    all     0.0747
iprec_at_recall_0.80    all     0.0573
iprec_at_recall_0.90    all     0.0377
iprec_at_recall_1.00    all     0.0130
P_5                     all     0.4960
P_10                    all     0.4720
P_15                    all     0.4400
P_20                    all     0.4100
P_30                    all     0.3440
P_100                   all     0.2004
P_200                   all     0.1394
P_500                   all     0.0775
P_1000                  all     0.0481


BM25Similarity and CustomAnalyzer
Trec eval results(with query title and description expansion):
runid                   all     com.example.CustomAnalyzer
num_q                   all     25
num_ret                 all     24085
num_rel                 all     2132
num_rel_ret             all     1106
map                     all     0.1458
gm_map                  all     0.0749
Rprec                   all     0.2088
bpref                   all     0.2384
recip_rank              all     0.4665
iprec_at_recall_0.00    all     0.5169
iprec_at_recall_0.10    all     0.3432
iprec_at_recall_0.20    all     0.2496
iprec_at_recall_0.30    all     0.1988
iprec_at_recall_0.40    all     0.1642
iprec_at_recall_0.50    all     0.1319
iprec_at_recall_0.60    all     0.0816
iprec_at_recall_0.70    all     0.0646
iprec_at_recall_0.80    all     0.0560
iprec_at_recall_0.90    all     0.0358
iprec_at_recall_1.00    all     0.0140
P_5                     all     0.2480
P_10                    all     0.2440
P_15                    all     0.2400
P_20                    all     0.2340
P_30                    all     0.2147
P_100                   all     0.1392
P_200                   all     0.1076
P_500                   all     0.0705
P_1000                  all     0.0442
