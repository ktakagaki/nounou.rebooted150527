function [sampFreq data3DInput] = readCCD2_GUI_PC(pathName, fileName)
% modified from readCCD1.m
%
% purpose: read in MiCAM02 CCD data (gsh = header file, gsd = data file)
% into the ConvertLoadData3DInput GUI. Retrieves the sampling frequency
% from the gsd file header. 
% Saves data in the specified output folder (fOutputPath) 
% with the fileName of the input fpath and as the variable
% data3DInput for analysis. 
%
% implemented parallel computing
%
% inputs:
% fileName = file name of the *.rsh header file (with the extension string
%           '.rsh' attached if not already present)
% pathName = path name of the *.rsh header file
% 
% outputs:
% sampFreq = sample frequency retrieved from the gsd file
% data3DInput = converted matlab 3D output of the acquisition file
% 
% version history: 
%   readCCD2_GUI_PC: take out IndivScale function from this conversion
%       process

tic 

fpath = fullfile(pathName, fileName);

%% Read the gsd file
fid = fopen(fpath, 'r', 'l');

% retrieve form information
%
% number of pixels on X axis
fseek(fid, 256, 'bof');
numX = fread(fid, 1, 'int16');

% number of pixels on Y axis
fseek(fid, 258, 'bof');
numY = fread(fid, 1, 'int16');

% number of frames
fseek(fid, 268, 'bof');
numFrames = fread(fid, 1, 'int16');

% sampling rate (msec)
fseek(fid, 284, 'bof');
sampRate = fread(fid, 1, 'float');
sampFreq = 1 / sampRate * 1000;

% average 
fseek(fid, 280, 'bof');
average = fread(fid, 1, 'float');

% read in optical mapping signals
fseek(fid, 972, 'bof');
fdata = fread(fid, numX*numY*numFrames, 'int16');
fclose(fid);


% reshape as 
%   each column = each frame, and 
%   each row contain the contents of each frames' channels

% retrieve new numX 
fdata = reshape(fdata, numX*numY, numFrames);

gshData = NaN(numY, numX, numFrames-1);

% *** modified to include waitbar **
t = waitbar(0, ['Converting *.gsd file: ' fileName]);
% one step: converting
steps = 1;


% create a background image from the first frame
oneframe = fdata(:, 1);
bkgdImage = reshape(oneframe, numX, numY)';

% the differential data is located in the 2nd frame on to the end
parfor j = 2:numFrames
    % retrieve one frame from the fdata
    oneframe = fdata(:, j);
    % when reshaping the data from the dataset, use
    %   row = numX,
    %   column = numY
    oneframe = reshape(oneframe, numX, numY);
    
    % however, to match up with what is actually seen, transpose
    oneframe = oneframe';
    
    gshData(:, :, j-1) = -(oneframe*100)./(bkgdImage*average);
    
end

% rename gshData and clear it
data3DInput = gshData;
clear gshData;

counter = 1;

waitbar(counter / steps, t, ['Finished Converting *.gsd file: ' fileName]);

disp(['Finished converting ' fileName])
FinalTime = num2str(toc);
disp(['Conversion took: ' FinalTime ' sec.'])

% close the waitbar
close(t) 