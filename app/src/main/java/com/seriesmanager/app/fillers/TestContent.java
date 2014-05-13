package com.seriesmanager.app.fillers;

import com.seriesmanager.app.adapters.CalendarAdapter;
import com.seriesmanager.app.entities.Episode;
import com.seriesmanager.app.entities.Season;
import com.seriesmanager.app.entities.Show;
import com.seriesmanager.app.entities.ShowSummary;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestContent {

    public static List<CalendarAdapter.ParentGroup> LIST_GROUPS = new ArrayList<CalendarAdapter.ParentGroup>();
    public static List<Show> SHOWS = new ArrayList<Show>();
    public static Map<Integer, Show> SHOWS_MAP = new HashMap<Integer, Show>();
    public static List<Episode> EPISODE_LIST = new ArrayList<Episode>();
    public static List<ShowSummary> START_SHOW_LIST = new ArrayList<ShowSummary>();
    public static List<Show> SEARCH_LIST = new ArrayList<Show>();
    public static List<Show> OVERDUE_LIST = new ArrayList<Show>();

    static {

        LIST_GROUPS.add(new CalendarAdapter.ParentGroup("Previous"));
        LIST_GROUPS.add(new CalendarAdapter.ParentGroup("Today"));
        LIST_GROUPS.add(new CalendarAdapter.ParentGroup("Soon"));

        START_SHOW_LIST.add(new ShowSummary(94571, "Community", "A smart comedy series about higher education – and lower expectations."));
        START_SHOW_LIST.add(new ShowSummary(153021, "The Walking Dead", "Tells the story of a small group of survivors living in the aftermath of a zombie apocalypse."));
        START_SHOW_LIST.add(new ShowSummary(75897, "South Park", "South Park is an animated series featuring four boys who live in the Colorado town of South Park, which is beset by frequent odd occurrences. Has become an award-winning show that is a unique blend of humor and satire."));

        Show sh1 = new Show(75897, "South Park", "South Park is an animated series featuring four boys who live in the Colorado town of South Park, which is beset by frequent odd occurrences. Has become an award-winning show that is a unique blend of humor and satire.",
                true, new Date(1997, 8, 1), "Comedy Central");
        sh1.setNumberOverdue(2);
        Show sh2 = new Show(153021, "The Walking Dead", "Tells the story of a small group of survivors living in the aftermath of a zombie apocalypse.",
                true, new Date(2010, 10, 31), "AMC");
        sh2.setNumberOverdue(5);

        SEARCH_LIST.add(sh1);
        SEARCH_LIST.add(sh2);

        OVERDUE_LIST.add(sh1);
        OVERDUE_LIST.add(sh2);

        addShow(new Show(94571, "Community", "A smart comedy series about higher education – and lower expectations.",
                new Date(2009, 9, 17), "NBC", false));
        addShow(sh2);
        addShow(new Show(258618, "Men at Work", "When Milo is thrust into single life after discovering his girlfriend has been cheating, he finds comfort in three best friends who help reinvent his manhood with the promise of sex, alcohol and some serious bromance.",
                new Date(2012, 5, 24), "TBS"));
        addShow(new Show(78901, "Supernatural", "Two brothers follow their father's footsteps as \"hunters\" fighting evil supernatural beings of many kinds including monsters, demons, and gods that roam the earth.",
                true, new Date(2005, 9, 13), "The CW"));
        addShow(sh1);
        addShow(new Show(359669, "Da Vinci's Demons", "The series follows the \"untold\" story of Leonardo Da Vinci: the genius during his early years in Renaissance Florence. As a 25-year old artist, inventor, swordsman, lover, dreamer and idealist, he struggles to live within the confines of his own reality and time as he begins to not only see the future, but invent it.",
                new Date(2013, 4, 12), "Starz!"));
        addShow(new Show(79349, "Dexter", "He's smart, he's good looking, and he's got a great sense of humor. He's Dexter Morgan, everyone's favorite serial killer. As a Miami forensics expert, he spends his days solving crimes, and nights committing them. But Dexter lives by a strict code of honor that is both his saving grace and lifelong burden.",
                new Date(2006, 10, 1), "Showtime"));
        //addShow(new Show(, "", "", new Date(, , ), ""));
    }

    static {
        Show s = SHOWS_MAP.get(94571);
        s.getSeasons().put(0, new Season(s, 0));
        s.getSeasons().put(1, new Season(s, 1));
        s.getSeasons().put(2, new Season(s, 2));
        s.getSeasons().put(3, new Season(s, 3));
        s.getSeasons().put(4, new Season(s, 4));
        s.getSeasons().put(5, new Season(s, 5));
        s = SHOWS_MAP.get(153021);
        s.getSeasons().put(0, new Season(s, 0));
        s.getSeasons().put(1, new Season(s, 1));
        s.getSeasons().put(2, new Season(s, 2));
        s.getSeasons().put(3, new Season(s, 3));
        s.getSeasons().put(4, new Season(s, 4));
        s = SHOWS_MAP.get(258618);
        s.getSeasons().put(0, new Season(s, 0));
        s.getSeasons().put(1, new Season(s, 1));
        s.getSeasons().put(2, new Season(s, 2));
        s.getSeasons().put(3, new Season(s, 3));
        s = SHOWS_MAP.get(78901);
        s.getSeasons().put(0, new Season(s, 0));
        s.getSeasons().put(1, new Season(s, 1));
        s.getSeasons().put(2, new Season(s, 2));
        s.getSeasons().put(3, new Season(s, 3));
        s.getSeasons().put(4, new Season(s, 4));
        s.getSeasons().put(5, new Season(s, 5));
        s.getSeasons().put(6, new Season(s, 6));
        s.getSeasons().put(7, new Season(s, 7));
        s.getSeasons().put(8, new Season(s, 8));
        s.getSeasons().put(9, new Season(s, 9));
        s = SHOWS_MAP.get(75897);
        s.getSeasons().put(0, new Season(s, 0));
        s.getSeasons().put(1, new Season(s, 1));
        s.getSeasons().put(2, new Season(s, 2));
        s.getSeasons().put(3, new Season(s, 3));
        s.getSeasons().put(4, new Season(s, 4));
        s.getSeasons().put(5, new Season(s, 5));
        s.getSeasons().put(6, new Season(s, 6));
        s.getSeasons().put(7, new Season(s, 7));
        s.getSeasons().put(8, new Season(s, 8));
        s.getSeasons().put(9, new Season(s, 9));
        s.getSeasons().put(10, new Season(s, 10));
        s.getSeasons().put(11, new Season(s, 11));
        s.getSeasons().put(12, new Season(s, 12));
        s.getSeasons().put(13, new Season(s, 13));
        s.getSeasons().put(14, new Season(s, 14));
        s.getSeasons().put(15, new Season(s, 15));
        s.getSeasons().put(16, new Season(s, 16));
        s.getSeasons().put(17, new Season(s, 17));
        s = SHOWS_MAP.get(359669);
        s.getSeasons().put(0, new Season(s, 0));
        s.getSeasons().put(1, new Season(s, 1));
        s.getSeasons().put(2, new Season(s, 2));
        s = SHOWS_MAP.get(79349);
        s.getSeasons().put(0, new Season(s, 0));
        s.getSeasons().put(1, new Season(s, 1));
        s.getSeasons().put(2, new Season(s, 2));
        s.getSeasons().put(3, new Season(s, 3));
        s.getSeasons().put(4, new Season(s, 4));
        s.getSeasons().put(5, new Season(s, 5));
        s.getSeasons().put(6, new Season(s, 6));
        s.getSeasons().put(7, new Season(s, 7));
        s.getSeasons().put(8, new Season(s, 8));
    }

    static {
        Show s = SHOWS_MAP.get(75897);
        Map<Integer, Season> ts = s.getSeasons();
        Season t = ts.get(1);
        E = t.getEpisodes();
        addEpisode(1, new Episode(1, s, t, 1, "Cartman Gets an Anal Probe", new Date(1997, 8, 13), true, "While the boys are waiting for the school bus, Cartman explains the odd nightmare he had the previous night involving alien visitors abducting him from his bed. Meanwhile Kyle and Stan try to convince Cartman that the dream was in fact a reality."));
        addEpisode(2, new Episode(2, s, t, 2, "Weight Gain 4000", new Date(1997, 8, 20), true, "Mr. Garrison starts the day off with a special announcement; one of South Park's own has won a national essay contest. Everyone is shocked to learn that the winner is Cartman, who doesn't even remember what he wrote about."));
        addEpisode(3, new Episode(3, s, t, 3, "Volcano", new Date(1997, 8, 27), true, "The boys get together with Stan's Uncle Jimbo and Jimbo's war-buddy Ned for a hunting trip in the nearby mountains. After Cartman's mom makes sure her \"little\" boy is safe and sound (much to Cartman's dismay) the group heads off for adventure. As they drive away from the town, Jimbo explains the \"finer\" points of hunting."));
        addEpisode(4, new Episode(4, s, t, 4, "Big Gay Al's Big Gay Boat Ride", new Date(1997, 9, 03), false, "Stan's got a new dog-named Sparky. Stan proclaims his pet to be the toughest dog on the mountain. Though while Stan thinks his dog is ready for fighting, Sparky proceeds to hump Sylvester like a little bitch. The other boys taunt Stan about his gay dog and Sparky runs away to find someone who will love him for who he is."));
        addEpisode(5, new Episode(5, s, t, 5, "An Elephant Makes Love to a Pig", new Date(1997, 9, 10), false, "Stan notices a new addition in their bus stop crew, an elephant standing alongside Kyle. At school, class lessons for the day involve genetic engineering (conveniently enough). Kyle questions the possibility of a genetically engineered elephant made smaller by design. The boys have to beat the rough kids in their genetics experiment, and will try anything to do it."));
        addEpisode(6, new Episode(6, s, t, 6, "Death", new Date(1997, 9, 17), true, "\"Happy Birthday\" to Stan's Grandpa Marsh who just turned 102. How does the wizened Mr. Marsh feel about his birthday? He sums it up in five words; \"I wish I were dead.\" and proceeds trying to end his life enlisting Stan to do it for him. However, Stan is aware of a very important thing if he kills his grandfather, he'd be in trouble, and if he's in trouble he can't watch Terrance and Phillip."));
        t = ts.get(2);
        E = t.getEpisodes();
        addEpisode(1, new Episode(7, s, t, 1, "Terrance & Phillip in 'Not Without My Anus'", new Date(1998, 5, 16), true, "The show starts off with the build up from last season's cliffhanger revolving around Cartman's paternal origins. However, just as it seems they are about to get on with the second half of \"Cartman's Mom Is A Dirty Slut,\" they do a 180 degree turn-around and reveal that for April Fool's Day they will be showing Terrance & Phillip in \"Not Without My Anus.\""));
        addEpisode(2, new Episode(8, s, t, 2, "Cartman's Mom is a Dirty Slut", new Date(1998, 5, 18), true, "Dr. Mephisto is about to reveal who Eric Cartman's father really is! Glass shatters and gunshots ring out. A mysterious assailant apparently shot Mephisto while the lights were off. Much to Eric's chagrin, they rush Mephisto off to the hospital. Chef rushes out the door with Mephisto in hand. They must fight against time to learn Eric's background, who is his father? Where does he come from?"));
        addEpisode(3, new Episode(9, s, t, 3, "Chickenlover", new Date(1998, 5, 20), true, "The Booktastic bus is in town and soon everyone finds out that Officer Barbrady can't read. He is whisked off to school with the boys so he can save his job. While he's there someone in South Park is having sex with the town's chickens and only Officer Barbrady with his new deputies Kenny, Kyle, Stan, and Cartman can stop them!"));
        addEpisode(4, new Episode(10, s, t, 4, "Ike's Wee Wee", new Date(1998, 5, 21), false, "Ike's going to have a Briss and everyone is invited! Stan, Kenny, and Cartman find out what a Briss really is and try to warn Kyle that his parents are going to cut off Ike's wee wee. Kyle sends Ike away to protect him from his scissor wielding parents."));
        addEpisode(5, new Episode(11, s, t, 5, "Conjoined Fetus Lady", new Date(1998, 5, 22), true, "Kyle is injured and must go to see the school nurse and is terrified because of the stories he's heard. She looks fairly normal, except for the dead fetus sticking out of her head. Kyle's mother educates the boys on her \"condition\" and decides that everyone in South Park should be made aware. Meanwhile, the South Park Cows dodgeball team goes to the state, national and finally the international finals in China."));
        addEpisode(6, new Episode(12, s, t, 6, "The Mexican Staring Frog of Southern Sri Lanka", new Date(1998, 6, 10), false, "Mr. Garrison assigns the boys to learn about Vietnam from somebody they know. Stan's Uncle Jimbo and his friend Ned tell the boys their outrageous tale. Mr. Garrison doesn't believe the boy's report and gives them detention. They plot revenge against Stan's Uncle by submitting a phony video of the Mexican Staring Frog of Southern Sri Lanka to them."));
        addEpisode(6, new Episode(13, s, t, 7, "City on the Edge of Forever (a.k.a. Flashbacks)", new Date(1998, 6, 17), false, "While their school bus is dangling on the edge of a cliff, the boys remember some of their past adventures. Mrs. Crabtree catches a ride into town with a stranger and winds up doing a brief stint as a standup comedienne. Back in South Park, Mr. Mackey convinces the parents that their missing children must have run away. In the end it's all a dream within a dream."));
        addEpisode(6, new Episode(14, s, t, 8, "Summer Sucks", new Date(1998, 6, 24), false, "Schools out for summer and with Mr. Hat missing, Mr. Garrison is over the edge. The state has a ban on the sale of fireworks, and it screws up the boy's plans for a 4th of July celebration. Mr. Garrison seeks psychiatric help from Dr. Katz while Jimbo and Ned go to Mexico, in an attempt to smuggle fireworks back to the children of America. "));
        addEpisode(6, new Episode(15, s, t, 9, "Chef's Chocolate Salty Balls", new Date(1998, 8, 19), false, "A film festival moves to South Park, only to have a devastating effect on the sewer system. Mr. Hankey calls upon Kyle for help. The movie people interpret Kyle's pleading as a pitch and they quickly turn his story into a film starring Tom Hanks and a monkey they call \"Mr. Hankey.\" Kyle is outraged that the filmmakers aren't hearing his pleas. Soon Chef's Salty Chocolate Balls energizes everyone."));
    }

    private static Map<Integer, Episode> E;

    private static void addShow(Show item) {
        SHOWS.add(item);
        SHOWS_MAP.put(item.getId(), item);
    }

    private static void addEpisode(int i, Episode ep) {
        EPISODE_LIST.add(ep);
        E.put(i, ep);
    }
}
