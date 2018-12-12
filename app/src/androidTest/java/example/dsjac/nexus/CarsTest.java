package example.dsjac.nexus;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CarsTest {

    @Rule
    public ActivityTestRule<Cars> mActivityTestRule = new ActivityTestRule<>(Cars.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void carsTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.place_autocomplete_search_input),
                        childAtPosition(
                                allOf(withId(R.id.place_autocomplete_fragment),
                                        childAtPosition(
                                                withId(R.id.relLayout1),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.place_autocomplete_search_input),
                        childAtPosition(
                                allOf(withId(R.id.place_autocomplete_fragment),
                                        childAtPosition(
                                                withId(R.id.relLayout1),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.place_autocomplete_search_input),
                        childAtPosition(
                                allOf(withId(R.id.place_autocomplete_fragment),
                                        childAtPosition(
                                                withId(R.id.relLayout1),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.place_autocomplete_clear_button), withContentDescription("Clear search"),
                        childAtPosition(
                                allOf(withId(R.id.place_autocomplete_fragment),
                                        childAtPosition(
                                                withId(R.id.relLayout1),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.place_autocomplete_search_input),
                        childAtPosition(
                                allOf(withId(R.id.place_autocomplete_fragment),
                                        childAtPosition(
                                                withId(R.id.relLayout1),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.place_autocomplete_clear_button), withContentDescription("Clear search"),
                        childAtPosition(
                                allOf(withId(R.id.place_autocomplete_fragment),
                                        childAtPosition(
                                                withId(R.id.relLayout1),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.map),
                        childAtPosition(
                                allOf(withId(R.id.constraint_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.lyft_button),
                        childAtPosition(
                                allOf(withId(R.id.constraint_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        linearLayout.check(matches(isDisplayed()));

        ViewInteraction frameLayout2 = onView(
                allOf(withId(R.id.uberRequestButton),
                        childAtPosition(
                                allOf(withId(R.id.constraint_layout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        frameLayout2.check(matches(isDisplayed()));

        ViewInteraction view = onView(
                allOf(withId(android.R.id.statusBarBackground),
                        childAtPosition(
                                IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                1),
                        isDisplayed()));
        view.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
