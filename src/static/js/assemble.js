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
        self.errors = ko.observable("");
        self.json = ko.pureComputed(function () {
            var instructionsObj = self.instructions().split("\n");
            var instructionGrammarsObj = self.instructionGrammars() ? JSON.parse(self.instructionGrammars()) : [];
            var macroGrammarsObj = self.macroGrammars() ? JSON.parse(self.macroGrammars()) : [];
            return {
                instructions: instructionsObj,
                instructionGrammars: instructionGrammarsObj,
                macroGrammars: macroGrammarsObj
            };
        }, this).extend({rateLimit: 500});

        self.post = function () {
            console.log("posting data: ");
            console.log(self.json());
            $.ajax({
                url: "http://localhost:8080/api/v3/assemble",
                type: "POST",
                // hack: http://stackoverflow.com/questions/1256593/why-am-i-getting-an-options-request-instead-of-a-get-request
                contentType: 'text/plain',
                data: JSON.stringify(self.json()),
                success: function (returnedData) {
                    var binaryJson = JSON.parse(returnedData);
                    self.binary(binaryJson.binary);
                    self.errors(binaryJson.errors);
                }
            });
        }
    };

    ko.applyBindings(new ViewModel());

}, 0);