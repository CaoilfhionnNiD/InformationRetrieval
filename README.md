To build the project: mvn package

To run the code: java -jar target/InformationRetrieval-1.0-SNAPSHOT.jar

Download GloVe word embedding vectors from here: https://nlp.stanford.edu/projects/glove/

BM25Similarity and StandardAnalyzer
Trec eval results:

runid                   all     or
num_q                   all     25
num_ret                 all     3146
num_rel                 all     2132
num_rel_ret             all     4
map                     all     0.0007
gm_map                  all     0.0000
Rprec                   all     0.0020
bpref                   all     0.0019
recip_rank              all     0.0580
iprec_at_recall_0.00    all     0.0580
iprec_at_recall_0.10    all     0.0000
iprec_at_recall_0.20    all     0.0000
iprec_at_recall_0.30    all     0.0000
iprec_at_recall_0.40    all     0.0000
iprec_at_recall_0.50    all     0.0000
iprec_at_recall_0.60    all     0.0000
iprec_at_recall_0.70    all     0.0000
iprec_at_recall_0.80    all     0.0000
iprec_at_recall_0.90    all     0.0000
iprec_at_recall_1.00    all     0.0000
P_5                     all     0.0240
P_10                    all     0.0160
P_15                    all     0.0107
P_20                    all     0.0080
P_30                    all     0.0053
P_100                   all     0.0016
P_200                   all     0.0008
P_500                   all     0.0003
P_1000                  all     0.0002

