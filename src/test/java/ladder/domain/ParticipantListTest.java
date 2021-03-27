package ladder.domain;

import ladder.Util;
import ladder.exception.CustomException;
import ladder.exception.ErrorCode;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ParticipantListTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "a,b,c,d,e",
            "A, b, C ,d",
            "a,aa,aaa,aaaa,aaaaa"})
    @DisplayName("정상적인 이름들을 승인할 수 있다")
    void validatesValidParticipants(String inputString) {
        List<String> verifiedParticipants = new ParticipantList(inputString).dto();
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
        CustomException thrown = assertThrows(CustomException.class, () -> new ParticipantList(inputString));
        assertThat(thrown.errorCode()).isEqualTo(ErrorCode.INVALID_PARTICIPANTS_INPUT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asdfgh", "longName", "INVALID,PARTICIPANTS,NAME"})
    @DisplayName("알파벳이 아닐 경우 INVALID_PARTICIPANTS_NAME 에러를 던진다")
    void abnormallyLongNameThrowsException(String inputString) {
        CustomException thrown = assertThrows(CustomException.class, () -> new ParticipantList(inputString));
        assertThat(thrown.errorCode()).isEqualTo(ErrorCode.INVALID_PARTICIPANTS_NAME);
    }
}
