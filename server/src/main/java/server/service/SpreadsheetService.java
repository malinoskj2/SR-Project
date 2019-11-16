package server.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import server.model.db.AnswerAttempt;
import server.model.db.Question;
import server.model.db.TestSubmission;
import server.repository.QuestionRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;


@Service
public class SpreadsheetService {


    private Optional<TestSubmission> submissionOptional;
    private List<AnswerAttempt> attempts;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private Environment env;

    Logger logger = LoggerFactory.getLogger(SpreadsheetService.class);

    public SpreadsheetService(Optional<TestSubmission> submission,
                              List<AnswerAttempt> attempts) {
        this.submissionOptional = submission;
        this.attempts = attempts;
    }

    public FileSystemResource convertToSpreadsheet() {




        Workbook wb = new HSSFWorkbook();//WorkbookFactory.create(new File("-spreadsheet.xls"));
        if (submissionOptional.isPresent()) {
            File f = new File(System.getProperty("user.dir") + "/spreadsheet" + submissionOptional.get().getPatientID() +".xls");
            TestSubmission submission = submissionOptional.get();
            Sheet sheet = wb.createSheet("raw data");
            Sheet sheet_derived_data = wb.createSheet("derived data");
            Row row1_labels = sheet.createRow(0);
            Row row2_data = sheet.createRow(1);
            row1_labels.createCell(0).setCellValue("testID");
            row2_data.createCell(0).setCellValue(submission.getTestID());
            row1_labels.createCell(1).setCellValue("examinerID");
            row2_data.createCell(1).setCellValue(submission.getExamID());
            row1_labels.createCell(2).setCellValue("patientID");
            row2_data.createCell(2).setCellValue(submission.getPatientID());
            //row1_labels.createCell(4).setCellValue("date");
            //row2_data.createCell(4).setCellValue(submission.getCreatedDate().toString());

            Row row3_resultLabels = sheet.createRow(2);
            row3_resultLabels.createCell(1).setCellValue("correct 1");
            row3_resultLabels.createCell(2).setCellValue("angle 1");
            row3_resultLabels.createCell(3).setCellValue("guess 1");
            row3_resultLabels.createCell(4).setCellValue("time 1");
            row3_resultLabels.createCell(5).setCellValue("correct 2");
            row3_resultLabels.createCell(6).setCellValue("angle 2");
            row3_resultLabels.createCell(7).setCellValue("guess 2");
            row3_resultLabels.createCell(8).setCellValue("time 2");
            row3_resultLabels.createCell(9).setCellValue("oblique angles (3,4 + 8,9)");

            Iterable<Question> questionIterable = questionRepository.findAll();
            //Iterator<Question> questionIterator = questionRepository.findAll().iterator();
            int questionNumber = 1;
            for (AnswerAttempt attempt : attempts) {
               // Optional<Question> questionOptional = questionRepository.findByQuestionid(1);//attempt.getQuestionID());
                //if(questionOptional.isPresent()) {
                    //Question question = questionIterator.next();

                    Row row_question_results = sheet.createRow(questionNumber + 2);

                    row_question_results.createCell(0).setCellValue("q" + questionNumber);
                    /*
                    row_question_results.createCell(1).setCellValue(question.getCorrectAngle1().equals
                                                                        (attempt.getGuessedAngle1()));
                    row_question_results.createCell(2).setCellValue(question.getCorrectAngle1());
                    row_question_results.createCell(3).setCellValue(attempt.getGuessedAngle1());
                    row_question_results.createCell(4).setCellValue(attempt.getTime1());
                    row_question_results.createCell(5).setCellValue(question.getCorrectAngle2().equals
                                                                        (attempt.getGuessedAngle2()));
                    row_question_results.createCell(6).setCellValue(question.getCorrectAngle2());
                    row_question_results.createCell(7).setCellValue(attempt.getGuessedAngle2());
                    row_question_results.createCell(8).setCellValue(attempt.getTime2());
                    row_question_results.createCell(9).setCellValue(
                                        question.getCorrectAngle1().equals(3) && question.getCorrectAngle2().equals(4) ||
                                        question.getCorrectAngle1().equals(8) && question.getCorrectAngle2().equals(9)
                                         );

                    // */
                //}
                questionNumber++;
            }
            try (OutputStream fileOut = new FileOutputStream(f)){
                wb.write(fileOut);
                System.out.println("\nfile written " + f.getAbsolutePath());
            }catch (Exception e) {
                System.out.println("caught:" + e);
                e.printStackTrace();
            }
            return new FileSystemResource(f);
        }
        return new FileSystemResource(new File(System.getProperty("user.dir") + "/emptyspreadsheet"));
    }



}
