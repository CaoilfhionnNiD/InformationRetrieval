To build the project: mvn package

To run the code: java -jar target/InformationRetrieval-1.0-SNAPSHOT.jar

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
