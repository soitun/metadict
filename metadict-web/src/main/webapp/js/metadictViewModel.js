/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
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

function MetadictViewModel() {

    var self = this;

    /** Client for accessing Metadict's API */
    this.metadictClient = new MetadictClient("api");

    /** Indicator if the app is connected to a metadict instance */
    this.isConnected = ko.observable(false);

    /** Indicator if the app is currently loading a query (i.e. starting) */
    this.isLoading = ko.observable(false);

    /** Indicator if the app is currently loading inside the status modal */
    this.isLoadingStatus = ko.observable(false);

    /** Indicator if the app has encountered a global error */
    this.isError = ko.observable(false);

    /** Indicator if the current state is only temporal can be ignored by the user (e.g. query failure) */
    this.isTemporalError = ko.observable(false);

    /** Error message for the user */
    this.errorMessage = ko.observable();

    /** Observable for the entered query string */
    this.queryString = ko.observable();

    /** Observable for object with status texts */
    this.statusObject = ko.observable();

    /** Array of available dictionaries */
    this.dictionaries = ko.observableArray();

    /** Selected dictionaries for querying */
    this.selectedDictionaries = ko.observableArray([]);

    /** Grouped entry results */
    this.entryGroups = ko.observableArray([]);

    /** Search recommendations */
    this.similarRecommendations = ko.observableArray([]);

    /** External contents */
    this.externalContents = ko.observableArray([]);

    /** Computed string concatenation of selected dictionaries */
    this.selectedDictionariesString = ko.computed(function () {
        if (self.selectedDictionaries != undefined)
            return self.selectedDictionaries().join(",");
        return "";
    });

    this.buildLanguageString = function (language) {
        if (language == undefined)
            return "";
        var displayName = language.displayName;
        var dialect = language.dialectDisplayName;
        if (dialect != undefined)
            return displayName + " (" + dialect + ")";
        else
            return displayName
    };

    this.setError = function (message, temporalError) {
        this.isLoading(false);
        this.isLoadingStatus(false);
        this.isError(true);
        this.errorMessage(message);
        this.isTemporalError(temporalError)
    };

    this.initFromParameters = function () {
        var queryString = getParameterByName("queryString");
        var dictionaries = getParameterByName("dictionaries");
        if (queryString != undefined && queryString !== "")
            self.queryString(queryString);
        if (dictionaries != undefined && dictionaries !== "") {
            var dictionaryArray = dictionaries.split(",");
            if (dictionaryArray instanceof Array) {
                // Select all dictionaries only if they are available
                _.each(dictionaryArray, function (queryString) {
                    if (_.findIndex(self.dictionaries(), function (d) {
                            return d.queryStringWithDialect == queryString
                        }) >= 0) {
                        self.selectedDictionaries.push(queryString)
                    } else {
                        var reversed = queryString.split("-").reverse().join("-")
                        if (_.findIndex(self.dictionaries(), function (d) {
                                return d.queryStringWithDialect == reversed
                            }) >= 0) {
                            self.selectedDictionaries.push(reversed)
                        }
                    }
                });
            }
        }
    };

    this.reloadDictionaries = function () {
        this.isLoading(true);
        self.metadictClient.getBidirectedDictionaries(self.reloadDictionariesSuccessCallback, self.genericErrorCallback)
    };

    this.genericErrorCallback = function (message) {
        $(".modal").closeModal();
        self.setError(message)
    };

    this.reloadDictionariesSuccessCallback = function (responseData) {
        self.isLoading(false);
        if (responseData == undefined || responseData.status == undefined || responseData.data == undefined) {
            self.setError("Internal system error")
        } else {
            self.dictionaries(responseData.data);
            self.initFromParameters();
            self.isConnected(true);
            $('.tooltipped').tooltip({delay: 50});
            // Submit query based on current get parameters
            if (self.selectedDictionariesString() != "" && self.queryString() != undefined && self.queryString() != "") {
                self.submitQuery();
            }
        }
    };

    this.closeError = function () {
        this.isError(false);
        this.isTemporalError(false);
        this.errorMessage(undefined);
        this.enableQueryElements();
    };

    this.disableQueryElements = function () {
        $("#query_input").prop("disable", true);
        $("#query_submit").prop("disable", true);
    };

    this.enableQueryElements = function () {
        $("#query_input").prop("disable", false);
        $("#query_submit").prop("disable", false);
    };

    /**
     * Toggle the selection state of a given dictionary object.
     *
     * @param dictionaryObject
     */
    this.toggleDictionarySelection = function (dictionaryObject) {
        if (dictionaryObject == undefined || dictionaryObject.queryStringWithDialect == undefined) {
            self.setError("Illegal data structure");
            return;
        }
        var queryString = dictionaryObject.queryStringWithDialect;
        var dictionaryIndex = _.indexOf(self.selectedDictionaries(), queryString);
        if (dictionaryIndex >= 0)
            self.selectedDictionaries(_.without(self.selectedDictionaries(), queryString))
        else
            self.selectedDictionaries.push(queryString);
        console.log(self.selectedDictionaries())
    };

    this.submitQuerySuccessCallback = function (response) {
        self.isLoading(false);
        try {
            console.log(response);
            var responseData = response.data;
            if (response.status !== "OK") {
                self.setError("The last query failed: " + response.status + " - " + (response.message != undefined) ? response.message : "", true);
                return;
            }
            if (!(responseData.groupedBilingualResults instanceof Array)
                || !(responseData.similarRecommendations instanceof Array)
                || !(responseData.externalContents instanceof Array)
            ) {
                self.setError("Illegal data structure", true);
                return;
            }
            self.entryGroups(responseData.groupedBilingualResults);
            self.similarRecommendations(responseData.similarRecommendations);
            self.externalContents(responseData.externalContents);
            console.log(self.externalContents());
            self.enableQueryElements();
            $('.tooltipped').tooltip({delay: 50});
        } catch (e) {
            console.log(e);
            self.setError(e);
        }
    };

    this.requery = function (queryString) {
        self.queryString(queryString);
        $("html, body").animate({scrollTop: "0"});
        self.submitQuery();
    };

    this.submitQuery = function () {
        try {
            if (self.queryString() == undefined || self.queryString().length <= 0) {
                Materialize.toast('No query entered', 4000);
                return;
            }
            if (self.selectedDictionaries() == undefined || self.selectedDictionaries().length <= 0) {
                Materialize.toast('No dictionaries selected', 4000);
                return;
            }

            self.isLoading(true);
            self.disableQueryElements();

            self.metadictClient.query(self.queryString(), self.selectedDictionaries(), self.submitQuerySuccessCallback, self.genericErrorCallback)
        } catch (e) {
            console.log(e);
            self.setError(e);
        }
    };

    this.resolveFlagClasses = function (languageObject) {
        if (languageObject == undefined || languageObject.identifier == undefined)
            return "mdi-alert-warning";
        var identifier = languageObject.identifier.substr(0, 2);
        var dialectIdentifier;
        if (dialectIdentifier != undefined)
            dialectIdentifier = languageObject.dialectIdentifier.substr(0, 2);
        // Custom mappings:
        if (identifier === "en") {
            if (dialectIdentifier === "us") {
                identifier = "us";
            } else {
                identifier = "gb";
            }
        }
        return "flag-icon flag-icon-" + identifier;
    };

    this.buildAdditionalDataString = function (data) {
        if (data.additionalRepresentation != undefined)
            return data.additionalRepresentation;
        else
            return "";
    };

    this.reloadStatus = function () {
        self.isLoadingStatus(true);
        self.metadictClient.getMainStatus(self.statusDialogSuccessCallback, self.genericErrorCallback)
    };

    this.statusDialogSuccessCallback = function (statusObject) {
        console.log(statusObject);
        self.statusObject(statusObject);
        console.log(self.statusObject());
        self.isLoadingStatus(false);
    };


    // Initialize the ViewModel (reload dictionaries and init from parameters!)
    this.reloadDictionaries();
}