/*
 * The MIT License
 *
 * Copyright 2015 Sergio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.lajuderia.beans;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author Sergio
 */
public class IGDBInformation {
    private class Time2Beat {
        private float _hastily;
        private float _normally;
        private float _completely;

        public float getCompletely() {
            return _completely;
        }

        public float getHastily() {
            return _hastily;
        }

        public float getNormally() {
            return _normally;
        }

        public boolean hasCompletely() {
            return _completely != 0;
        }

        public boolean hastHastily() {
            return _hastily != 0;
        }

        public boolean hasNormally() {
            return _normally != 0;
        }

        public Time2Beat(float hastily, float normally, float _completely){
            this._hastily = hastily;
            this._normally = normally;
            this._completely = _completely;
        }

        @Override
        public int hashCode() {
            int hash = 23;
            hash = 29 * hash + Float.floatToIntBits(_hastily);
            hash = 29 * hash + Float.floatToIntBits(_normally);
            hash = 29 * hash + Float.floatToIntBits(_completely);

            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return (
                    (obj != null)
                    && (obj instanceof Time2Beat)
                    && (_hastily == ((Time2Beat) obj)._hastily)
                    && (_normally == ((Time2Beat) obj)._normally)
                    && (_completely == ((Time2Beat) obj)._completely)
                    );
        }
    }

    private int mId;
    private String mTitle;
    private String mSummary;
    private String mStoryLine;
    private String mGenre;
    private float mRating;
    private Time2Beat mTime2Beat;
    private String mCoverCloudinaryId;
    private float mAggregatedRating;

    public IGDBInformation(){
    }
    
    public IGDBInformation(int id, String title, String summary, String storyLine, String genre,
                           float rating, float aggregatedRating, float t2bHastily, float t2bNormally, float t2bCompletely, String coverCloudinaryId){
        this.mId = id;
        this.mTitle = title;
        this.mSummary = summary;
        this.mStoryLine = storyLine;
        this.mGenre = genre;
        this.mRating = rating;
        this.mAggregatedRating = aggregatedRating;
        this.setTimeToBeat(t2bHastily, t2bNormally, t2bCompletely);
        this.mCoverCloudinaryId = coverCloudinaryId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getStoryLine() {
        return mStoryLine;
    }

    public void setStoryLine(String _storyLine) {
        this.mStoryLine = _storyLine;
    }

    /**
     * @return the mTitle
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @param title the mTitle to set
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * @return the mGenre
     */
    public String getGenre() {
        return mGenre;
    }

    /**
     * @param _genre the mGenre to set
     */
    public void setGenre(String _genre) {
        this.mGenre = _genre;
    }

    /**
     * @return the mSummary
     */
    public String getSummary() {
        return mSummary;
    }

    /**
     * @param _summary the mSummary to set
     */
    public void setSummary(String _summary) {
        this.mSummary = _summary;
    }

    /**
     * @return the mRating
     */
    public float getRating() { return (mRating); }

    /**
     * @param rating the mRating to set
     */
    public void setRating(float rating) {
        this.mRating = rating;
    }

    public String getCoverCloudinaryId() {
        return mCoverCloudinaryId;
    }

    public void setCoverCloudinaryId(String coverCloudinaryId) {
        this.mCoverCloudinaryId = coverCloudinaryId;
    }

    public void setTimeToBeat(float hastily, float normally, float completely) {
        this.mTime2Beat = new Time2Beat(hastily, normally, completely);
    }

    public float getHastilyT2B() {
        return (
                mTime2Beat.hastHastily()
                    ? mTime2Beat.getHastily()
                    : mTime2Beat.hasNormally()
                        ? mTime2Beat.getNormally()
                        : mTime2Beat.getCompletely()
        );
    }

    public float getNormallyT2B() {
        return (
                mTime2Beat.hasNormally()
                        ? mTime2Beat.getNormally()
                        : mTime2Beat.hasCompletely()
                            ? mTime2Beat.getCompletely()
                            : mTime2Beat.getHastily()
        );
    }

    public float getCompletelyT2B() {
        return (
                mTime2Beat.hasCompletely()
                        ? mTime2Beat.getCompletely()
                        : mTime2Beat.hasNormally()
                            ? mTime2Beat.getNormally()
                            : mTime2Beat.getHastily()
        );
    }

    public float getAggregatedRating() {
        return mAggregatedRating;
    }

    public void setAggregatedRating(float mAggregatedRating) {
        this.mAggregatedRating = mAggregatedRating;
    }

    @Override
    public String toString(){
        DecimalFormat df = new DecimalFormat("##.#");
            df.setRoundingMode(RoundingMode.DOWN);

        return ( new StringBuilder()
                .append(mTitle)
                .append(" (R:")
                .append(df.format(mRating))
                .append(")")
                .toString() );
    }
    
    @Override
    public boolean equals(Object obj) {
        return (
                (obj != null)
                && (obj instanceof IGDBInformation)
                && (this.mId == ((IGDBInformation) obj).mId)
                && (this.mTitle.equals(((IGDBInformation) obj).mTitle))
                && (this.mSummary.equals(((IGDBInformation) obj).mSummary))
                && (this.mStoryLine.equals(((IGDBInformation) obj).mStoryLine))
                && (this.mGenre.equals(((IGDBInformation) obj).mGenre))
                && (this.mRating == ((IGDBInformation) obj).mRating)
                && (this.mTime2Beat.equals(((IGDBInformation) obj).mTime2Beat))
                );        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.mTitle != null ? this.mTitle.hashCode() : 0);
        hash = 89 * hash + (this.mSummary != null ? this.mSummary.hashCode() : 0);
        hash = 89 * hash + (this.mStoryLine != null ? this.mStoryLine.hashCode() : 0);
        hash = 89 * hash + (this.mGenre != null ? this.mGenre.hashCode() : 0);
        hash = 89 * hash + Float.floatToIntBits(mRating);
        hash = 89 * hash + this.mTime2Beat.hashCode();
        return hash;
    }
}