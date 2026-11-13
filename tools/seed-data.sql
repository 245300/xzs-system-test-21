-- 学之思本地测试数据：一年级语文单选题 10 道 + 固定试卷 1 套
-- 说明：question.score 存的是展示分 ×10，50 表示 5 分；paper.score 为题目分值合计。
-- 仅用于本地 8065 测试环境，重复执行前请先清理 id >= 2 的题目与 id = 1 的试卷。

USE xzs;

INSERT INTO t_text_content (id, content, create_time) VALUES
(2,  '{"titleContent":"2+2等于几？","analyze":"2+2=4。","questionItemObjects":[{"prefix":"A","content":"3","score":null,"itemUuid":null},{"prefix":"B","content":"4","score":null,"itemUuid":null},{"prefix":"C","content":"5","score":null,"itemUuid":null},{"prefix":"D","content":"6","score":null,"itemUuid":null}],"correct":"B"}', NOW()),
(3,  '{"titleContent":"3+3等于几？","analyze":"3+3=6。","questionItemObjects":[{"prefix":"A","content":"5","score":null,"itemUuid":null},{"prefix":"B","content":"6","score":null,"itemUuid":null},{"prefix":"C","content":"7","score":null,"itemUuid":null},{"prefix":"D","content":"8","score":null,"itemUuid":null}],"correct":"B"}', NOW()),
(4,  '{"titleContent":"下列哪位是古诗《静夜思》的作者？","analyze":"《静夜思》作者为李白。","questionItemObjects":[{"prefix":"A","content":"杜甫","score":null,"itemUuid":null},{"prefix":"B","content":"李白","score":null,"itemUuid":null},{"prefix":"C","content":"白居易","score":null,"itemUuid":null},{"prefix":"D","content":"王维","score":null,"itemUuid":null}],"correct":"B"}', NOW()),
(5,  '{"titleContent":"“春眠不觉晓”的下一句是？","analyze":"出自孟浩然《春晓》。","questionItemObjects":[{"prefix":"A","content":"处处闻啼鸟","score":null,"itemUuid":null},{"prefix":"B","content":"夜来风雨声","score":null,"itemUuid":null},{"prefix":"C","content":"花落知多少","score":null,"itemUuid":null},{"prefix":"D","content":"床前明月光","score":null,"itemUuid":null}],"correct":"A"}', NOW()),
(6,  '{"titleContent":"“一去二三里”出自哪首诗？","analyze":"出自邵雍《山村咏怀》。","questionItemObjects":[{"prefix":"A","content":"山村咏怀","score":null,"itemUuid":null},{"prefix":"B","content":"静夜思","score":null,"itemUuid":null},{"prefix":"C","content":"咏鹅","score":null,"itemUuid":null},{"prefix":"D","content":"春晓","score":null,"itemUuid":null}],"correct":"A"}', NOW()),
(7,  '{"titleContent":"下列哪个字是左右结构？","analyze":"“明”为左右结构。","questionItemObjects":[{"prefix":"A","content":"明","score":null,"itemUuid":null},{"prefix":"B","content":"日","score":null,"itemUuid":null},{"prefix":"C","content":"月","score":null,"itemUuid":null},{"prefix":"D","content":"山","score":null,"itemUuid":null}],"correct":"A"}', NOW()),
(8,  '{"titleContent":"“大”的反义词是？","analyze":"“大”的反义词是“小”。","questionItemObjects":[{"prefix":"A","content":"多","score":null,"itemUuid":null},{"prefix":"B","content":"小","score":null,"itemUuid":null},{"prefix":"C","content":"高","score":null,"itemUuid":null},{"prefix":"D","content":"长","score":null,"itemUuid":null}],"correct":"B"}', NOW()),
(9,  '{"titleContent":"一年有多少个月？","analyze":"一年有 12 个月。","questionItemObjects":[{"prefix":"A","content":"10","score":null,"itemUuid":null},{"prefix":"B","content":"11","score":null,"itemUuid":null},{"prefix":"C","content":"12","score":null,"itemUuid":null},{"prefix":"D","content":"13","score":null,"itemUuid":null}],"correct":"C"}', NOW()),
(10, '{"titleContent":"“白日依山尽”的下一句是？","analyze":"出自王之涣《登鹳雀楼》。","questionItemObjects":[{"prefix":"A","content":"黄河入海流","score":null,"itemUuid":null},{"prefix":"B","content":"欲穷千里目","score":null,"itemUuid":null},{"prefix":"C","content":"更上一层楼","score":null,"itemUuid":null},{"prefix":"D","content":"遥知不是雪","score":null,"itemUuid":null}],"correct":"A"}', NOW()),
(11, '[{"name":"一、单选题","questionItems":[{"id":1,"itemOrder":1},{"id":2,"itemOrder":2},{"id":3,"itemOrder":3},{"id":4,"itemOrder":4},{"id":5,"itemOrder":5},{"id":6,"itemOrder":6},{"id":7,"itemOrder":7},{"id":8,"itemOrder":8},{"id":9,"itemOrder":9},{"id":10,"itemOrder":10}]}]', NOW());

INSERT INTO t_question (id, question_type, subject_id, score, grade_level, difficult, correct, info_text_content_id, create_user, status, create_time, deleted) VALUES
(2,  1, 1, 50, 1, 1, 'B', 2,  2, 1, NOW(), 0),
(3,  1, 1, 50, 1, 1, 'B', 3,  2, 1, NOW(), 0),
(4,  1, 1, 50, 1, 2, 'B', 4,  2, 1, NOW(), 0),
(5,  1, 1, 50, 1, 2, 'A', 5,  2, 1, NOW(), 0),
(6,  1, 1, 50, 1, 2, 'A', 6,  2, 1, NOW(), 0),
(7,  1, 1, 50, 1, 1, 'A', 7,  2, 1, NOW(), 0),
(8,  1, 1, 50, 1, 1, 'B', 8,  2, 1, NOW(), 0),
(9,  1, 1, 50, 1, 1, 'C', 9,  2, 1, NOW(), 0),
(10, 1, 1, 50, 1, 2, 'A', 10, 2, 1, NOW(), 0);

INSERT INTO t_exam_paper (id, name, subject_id, paper_type, grade_level, score, question_count, suggest_time, limit_start_time, limit_end_time, frame_text_content_id, create_user, create_time, deleted, task_exam_id) VALUES
(1, '一年级语文基础测试', 1, 1, 1, 500, 10, 30, NULL, NULL, 11, 2, NOW(), 0, NULL);
