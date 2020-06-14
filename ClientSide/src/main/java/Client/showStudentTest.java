package Client;

import java.io.IOException;
import java.util.List;

import CloneEntities.CloneAnswerToQuestion;
import CloneEntities.CloneExam;
import CloneEntities.CloneQuestionInExam;
import CloneEntities.CloneStudentTest;
import CloneEntities.CloneTimeExtensionRequest;
import CloneEntities.CloneTimeExtensionRequest.RequestStatus;
import UtilClasses.DataElements.ClientToServerOpcodes;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

public class showStudentTest extends AbstractController {

	@FXML
	private TableView<CloneAnswerToQuestion> questionsTable;

	@FXML
	private TableColumn<CloneAnswerToQuestion, String> questionName;

	@FXML
	private TableColumn<CloneAnswerToQuestion, String> gradeCol;

	@FXML
	private TableColumn<CloneAnswerToQuestion, String> answerCol;

	@FXML
	private TableColumn<CloneAnswerToQuestion, String> pointsCol;

	@FXML
	private Label GradeLabel;

	@FXML
	private Label TestNameLabel;

	@FXML
	private TextArea TeacherCommentField;

	@FXML
	private Button ShowQuestionButton;

	private CloneExam thisExam;

	public void initialize() {
		questionName.setCellValueFactory(new PropertyValueFactory<CloneAnswerToQuestion, String>("Name"));

		gradeCol.setCellValueFactory(new PropertyValueFactory<CloneAnswerToQuestion, String>("CorrectAnswer"));

		answerCol.setCellValueFactory(new PropertyValueFactory<CloneAnswerToQuestion, String>("YourAnswer"));

		pointsCol.setCellValueFactory(new PropertyValueFactory<CloneAnswerToQuestion, String>("Points"));

		questionsTable.getColumns().setAll(questionName, answerCol, gradeCol, pointsCol);
	}

	public void setFields(CloneStudentTest st) throws InterruptedException {
		TeacherCommentField.setText(st.getExamCheckNotes());
		GradeLabel.setText(String.valueOf(st.getGrade()));
		TestNameLabel.setText(st.getTest().getExamToExecute().getExamName());
		TeacherCommentField.setText(st.getTest().getExamToExecute().getTeacherComments());

		questionsTable.setRowFactory(
				(Callback<TableView<CloneAnswerToQuestion>, TableRow<CloneAnswerToQuestion>>) new Callback<TableView<CloneAnswerToQuestion>, TableRow<CloneAnswerToQuestion>>() {
					@Override
					public TableRow<CloneAnswerToQuestion> call(TableView<CloneAnswerToQuestion> questionsTableView) {
						return new TableRow<CloneAnswerToQuestion>() {
							@Override
							protected void updateItem(CloneAnswerToQuestion item, boolean b) {
								super.updateItem(item, b);

								if (item == null)
									return;

								if (item.getCorrectAnswer().equals(item.getYourAnswer()))
									setStyle("-fx-background-color: #03fc35;");
								else
									setStyle("-fx-background-color: #ff0000;");
							}
						};
					}
				});

		thisExam = st.getTest().getExamToExecute();

		try {
			GetDataFromDB(ClientToServerOpcodes.GetAnswersToExamOfStudentTest, st);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Thread.sleep(150);

	}

	void setQuestions(ObservableList<CloneAnswerToQuestion> questions) {
		Platform.runLater(() -> {
			questionsTable.setItems(questions);
			try {
				GetDataFromDB(ClientToServerOpcodes.GetAllQuestionInExamRelatedToExam, thisExam);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	void setAnswers(List<CloneQuestionInExam> questions) {

		for (int i = 0; i < questionsTable.getItems().size(); i++) {
			questionsTable.getItems().get(i).setPoints(questions.get(i).getGrade());
		}
	}

	@FXML
	void onClickedQuestion(ActionEvent event) {
		if (questionsTable.getSelectionModel().getSelectedItem() != null) {
			Platform.runLater(() -> {
				Parent root = null;
				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("showQuestion.fxml"));
					root = (Parent) fxmlLoader.load();
					showQuestion q = fxmlLoader.getController();
					q.setFields(questionsTable.getSelectionModel().getSelectedItem().getQuestion());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Stage stage = new Stage();
				stage.setTitle(
						"Question " + questionsTable.getSelectionModel().getSelectedItem().getQuestion().getSubject());
				stage.setScene(new Scene(root));
				stage.show();
			});
		}
	}
}
