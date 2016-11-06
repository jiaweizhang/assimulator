/**
 * Created by Jiawei on 8/1/2016.
 */
setTimeout(function () {
    ko.bindingHandlers.ace = {
        init: function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            var editor = ace.edit(element.id);

            editor.setValue(ko.unwrap(valueAccessor()));
            editor.gotoLine(0);

            var options = allBindingsAccessor().aceOptions || {};
            if (options.theme) editor.setTheme("ace/theme/" + options.theme);
            if (options.mode) editor.getSession().setMode("ace/mode/" + options.mode);

            var sub = editor.getSession().on('change', function () {
                if (ko.isWriteableObservable(valueAccessor())) {
                    valueAccessor()(editor.getValue());
                }
            });

            ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
                editor.destroy();
            });

            // using computed (like an update) to maintain state (editor) between init and update
            ko.computed({
                read: function () {
                    var actualValue = editor.getValue();
                    var newValue = ko.unwrap(valueAccessor());
                    if (newValue !== actualValue) {
                        editor.setValue(newValue);
                    }
                }
            })
        }
    };

    var ViewModel = function () {
        var self = this;
        self.instructions = ko.observable("");
        self.instructionGrammars = ko.observable("");
        self.macroGrammars = ko.observable("");
        self.binary = ko.observable("");
        self.returnedErrors = ko.observable("");
        self.instructionGrammarErrors = ko.observable("");
        self.macroGrammarErrors = ko.observable("");
        self.errors = ko.computed(function() {
            return self.instructionGrammarErrors() + "\n" + self.macroGrammarErrors() + "\n" + self.returnedErrors();
        });
        self.json = ko.computed(function () {
            var instructionsObj = self.instructions().split("\n");
            var instructionGrammarsObj;
            try {
                instructionGrammarsObj = JSON.parse(self.instructionGrammars());
                self.instructionGrammarErrors("");
            }
            catch (err) {
                self.instructionGrammarErrors('Instruction Grammars: ' + err);
            }
            var macroGrammarsObj;
            try {
                macroGrammarsObj = JSON.parse(self.macroGrammars())
                self.macroGrammarErrors("");
            }
            catch (err) {
                self.macroGrammarErrors('Macro Grammars: ' + err);
            }
            return {
                instructions: instructionsObj,
                instructionGrammars: instructionGrammarsObj,
                macroGrammars: macroGrammarsObj
            };
        }, this).extend({rateLimit: 100});

        self.post = function () {
            console.log("posting data: ");
            console.log(self.json());
            $.ajax({
                url: "http://localhost:8080/api/v3/assemble",
                type: "POST",
                // hack: http://stackoverflow.com/questions/1256593/why-am-i-getting-an-options-request-instead-of-a-get-request
                contentType: 'application/json',
                data: JSON.stringify(self.json()),
                success: function (response) {
                    console.log(response);
                    var binaryJson = response.body;
                    self.binary(binaryJson.binary);
                    self.returnedErrors(binaryJson.errors);
                }
            });
        }
    };

    ko.applyBindings(new ViewModel());

}, 0);