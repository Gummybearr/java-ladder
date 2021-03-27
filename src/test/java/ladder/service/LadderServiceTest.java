package ladder.service;

import ladder.Util;
import ladder.domain.Ladder;
import ladder.exception.CustomException;
import ladder.exception.ErrorCode;
import ladder.view.InputView;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LadderServiceTest {

    private InputView inputView;

    private LadderService ladderService;

    @BeforeEach
    void setUp() {
        this.inputView = Mockito.mock(InputView.class);
        this.ladderService = new LadderService();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "a,b,c,d,e",
            "A, b, C ,d",
            "a,aa,aaa,aaaa,aaaaa"})
    @DisplayName("정상적인 이름들을 승인할 수 있다")
    void validatesValidParticipants(String inputString) {
        BDDMockito.given(inputView.participants()).willReturn(inputString);
        String participantString = inputView.participants();
        List<String> verifiedParticipants = ladderService.verifiedParticipants(participantString);
        List<String> expectedParticipants = Util.parsedStringList(inputString);

        SoftAssertions softAssertions = new SoftAssertions();
        for (int idx = 0; idx < expectedParticipants.size(); idx++) {
            softAssertions.assertThat(verifiedParticipants.get(idx)).isEqualTo(expectedParticipants.get(idx));
        }
        softAssertions.assertAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "!", "가"})
    @DisplayName("알파벳이 아닐 경우 INVALID_PARTICIPANTS_INPUT 에러를 던진다")
    void nonAlphabeticalInputThrowsException(String inputString) {
        BDDMockito.given(inputView.participants()).willReturn(inputString);
        String participantString = inputView.participants();
        CustomException thrown = assertThrows(CustomException.class, () -> ladderService.verifiedParticipants(participantString));
        assertThat(thrown.errorCode()).isEqualTo(ErrorCode.INVALID_PARTICIPANTS_INPUT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdfgh", "longName", "INVALID,PARTICIPANTS,NAME"})
    @DisplayName("알파벳이 아닐 경우 INVALID_PARTICIPANTS_NAME 에러를 던진다")
    void abnormallyLongNameThrowsException(String inputString) {
        BDDMockito.given(inputView.participants()).willReturn(inputString);
        String participantString = inputView.participants();
        CustomException thrown = assertThrows(CustomException.class, () -> ladderService.verifiedParticipants(participantString));
        assertThat(thrown.errorCode()).isEqualTo(ErrorCode.INVALID_PARTICIPANTS_NAME);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 99999, 0x7fffffff})
    @DisplayName("정상적인 이름들을 승인할 수 있다")
    void validatesLadderHeights(int inputInteger) {
        BDDMockito.given(inputView.maximumLadderHeight()).willReturn(inputInteger);
        int verifiedMaximumLadderHeight = ladderService.verifiedMaximumLadderHeight(inputInteger);
        assertThat(verifiedMaximumLadderHeight).isEqualTo(inputInteger);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -2, -3, -4, -5, -6, -7, -8, -9, -10, -99999, 0x80000000})
    @DisplayName("0이나 음수를 사다리 높이로 주면 INVALID_LADDER_HEIGHT을 던진다")
    void negativeInputThrowsException(int inputInteger) {
        BDDMockito.given(inputView.maximumLadderHeight()).willReturn(inputInteger);
        CustomException thrown = assertThrows(CustomException.class, () -> ladderService.verifiedMaximumLadderHeight(inputInteger));
        assertThat(thrown.errorCode()).isEqualTo(ErrorCode.INVALID_LADDER_HEIGHT);
    }

}
